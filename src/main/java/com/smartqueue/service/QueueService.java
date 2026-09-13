package com.smartqueue.service;

import com.smartqueue.dto.QueueResponse;
import com.smartqueue.dto.ServiceCounterResponse;
import com.smartqueue.entity.QueueEntry;
import com.smartqueue.entity.QueueStatus;
import com.smartqueue.entity.ServiceEntity;
import com.smartqueue.entity.User;
import com.smartqueue.exception.QueueException;
import com.smartqueue.exception.ResourceNotFoundException;
import com.smartqueue.repository.QueueEntryRepository;
import com.smartqueue.repository.ServiceRepository;
import com.smartqueue.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class QueueService {

    private final QueueEntryRepository queueEntryRepository;
    private final UserRepository userRepository;
    private final ServiceRepository serviceRepository;

    public QueueService(QueueEntryRepository queueEntryRepository, UserRepository userRepository, ServiceRepository serviceRepository) {
        this.queueEntryRepository = queueEntryRepository;
        this.userRepository = userRepository;
        this.serviceRepository = serviceRepository;
    }

    @Transactional
    public QueueResponse joinQueue(String username, Long serviceId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

        ServiceEntity service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with ID: " + serviceId));

        if (!service.isActive()) {
            throw new QueueException("This service is currently deactivated.");
        }

        LocalDate today = LocalDate.now();

        // Check if user already has an active queue entry for this service today
        List<QueueStatus> activeStatuses = List.of(QueueStatus.WAITING, QueueStatus.CALLED, QueueStatus.SERVING);
        Optional<QueueEntry> existingEntry = queueEntryRepository.findFirstByUserAndServiceAndQueueDateAndStatusIn(
                user, service, today, activeStatuses
        );

        if (existingEntry.isPresent()) {
            throw new QueueException("You are already in this queue.");
        }

        // Generate next token number dynamically
        long countToday = queueEntryRepository.countByServiceAndQueueDate(service, today);
        String prefix = generateServicePrefix(service.getServiceName());
        String tokenNumber = String.format("%s-%03d", prefix, countToday + 1);

        QueueEntry newEntry = new QueueEntry(tokenNumber, user, service, today, QueueStatus.WAITING);
        QueueEntry savedEntry = queueEntryRepository.save(newEntry);

        return mapToQueueResponse(savedEntry);
    }

    @Transactional
    public QueueResponse cancelQueue(String username, Long queueId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        QueueEntry queueEntry = queueEntryRepository.findById(queueId)
                .orElseThrow(() -> new ResourceNotFoundException("Queue entry not found with ID: " + queueId));

        if (!queueEntry.getUser().getId().equals(user.getId())) {
            throw new QueueException("You are not authorized to cancel this queue entry.");
        }

        if (queueEntry.getStatus() == QueueStatus.COMPLETED || queueEntry.getStatus() == QueueStatus.CANCELLED) {
            throw new QueueException("Queue entry is already " + queueEntry.getStatus().name().toLowerCase() + ".");
        }

        queueEntry.setStatus(QueueStatus.CANCELLED);
        QueueEntry updatedEntry = queueEntryRepository.save(queueEntry);
        return mapToQueueResponse(updatedEntry);
    }

    public List<QueueResponse> getUserActiveQueueStatus(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        LocalDate today = LocalDate.now();
        List<QueueStatus> activeStatuses = List.of(QueueStatus.WAITING, QueueStatus.CALLED, QueueStatus.SERVING);

        List<QueueEntry> activeEntries = queueEntryRepository.findByUserAndQueueDateOrderByJoinTimeDesc(user, today)
                .stream()
                .filter(e -> activeStatuses.contains(e.getStatus()))
                .collect(Collectors.toList());

        return activeEntries.stream().map(this::mapToQueueResponse).collect(Collectors.toList());
    }

    public List<QueueEntry> getUserQueueHistory(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
        return queueEntryRepository.findByUserOrderByJoinTimeDesc(user);
    }

    @Transactional
    public QueueResponse callNextToken(Long serviceId) {
        ServiceEntity service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with ID: " + serviceId));

        LocalDate today = LocalDate.now();

        // Enforce rule: If a token is currently SERVING, do not allow another token to be called until completed/skipped/cancelled
        Optional<QueueEntry> currentServing = queueEntryRepository.findFirstByServiceAndQueueDateAndStatusOrderByJoinTimeAsc(
                service, today, QueueStatus.SERVING
        );
        if (currentServing.isPresent()) {
            throw new QueueException("Token " + currentServing.get().getTokenNumber() + 
                    " is currently SERVING for " + service.getServiceName() + 
                    ". Please complete, skip, or cancel it before calling the next token.");
        }

        Optional<QueueEntry> nextWaiting = queueEntryRepository.findFirstByServiceAndQueueDateAndStatusOrderByJoinTimeAsc(
                service, today, QueueStatus.WAITING
        );

        if (nextWaiting.isEmpty()) {
            throw new QueueException("No waiting users in queue for service: " + service.getServiceName());
        }

        QueueEntry entry = nextWaiting.get();
        entry.setStatus(QueueStatus.SERVING);
        entry.setCalledTime(LocalDateTime.now());
        QueueEntry updated = queueEntryRepository.save(entry);

        return mapToQueueResponse(updated);
    }

    @Transactional
    public QueueResponse completeCurrentToken(Long serviceId) {
        ServiceEntity service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found: " + serviceId));

        LocalDate today = LocalDate.now();

        Optional<QueueEntry> currentServing = queueEntryRepository.findFirstByServiceAndQueueDateAndStatusOrderByJoinTimeAsc(
                service, today, QueueStatus.SERVING
        );

        if (currentServing.isEmpty()) {
            currentServing = queueEntryRepository.findFirstByServiceAndQueueDateAndStatusOrderByJoinTimeAsc(
                    service, today, QueueStatus.CALLED
            );
        }

        if (currentServing.isEmpty()) {
            throw new QueueException("No token currently being served for service: " + service.getServiceName());
        }

        QueueEntry entry = currentServing.get();
        entry.setStatus(QueueStatus.COMPLETED);
        entry.setCompletedTime(LocalDateTime.now());
        QueueEntry updated = queueEntryRepository.save(entry);

        return mapToQueueResponse(updated);
    }

    @Transactional
    public QueueResponse skipCurrentToken(Long serviceId) {
        ServiceEntity service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found: " + serviceId));

        LocalDate today = LocalDate.now();

        Optional<QueueEntry> activeToken = queueEntryRepository.findFirstByServiceAndQueueDateAndStatusOrderByJoinTimeAsc(
                service, today, QueueStatus.SERVING
        );

        if (activeToken.isEmpty()) {
            activeToken = queueEntryRepository.findFirstByServiceAndQueueDateAndStatusOrderByJoinTimeAsc(
                    service, today, QueueStatus.WAITING
            );
        }

        if (activeToken.isEmpty()) {
            throw new QueueException("No active or waiting token to skip for service: " + service.getServiceName());
        }

        QueueEntry entry = activeToken.get();
        entry.setStatus(QueueStatus.SKIPPED);
        entry.setCompletedTime(LocalDateTime.now());
        QueueEntry updated = queueEntryRepository.save(entry);

        return mapToQueueResponse(updated);
    }

    @Transactional
    public QueueResponse cancelTokenByAdmin(Long queueId) {
        QueueEntry queueEntry = queueEntryRepository.findById(queueId)
                .orElseThrow(() -> new ResourceNotFoundException("Queue entry not found with ID: " + queueId));

        queueEntry.setStatus(QueueStatus.CANCELLED);
        queueEntry.setCompletedTime(LocalDateTime.now());
        QueueEntry updatedEntry = queueEntryRepository.save(queueEntry);
        return mapToQueueResponse(updatedEntry);
    }

    public List<ServiceCounterResponse> getServiceCountersData() {
        LocalDate today = LocalDate.now();
        List<ServiceEntity> services = serviceRepository.findAll();
        List<ServiceCounterResponse> counters = new ArrayList<>();

        for (ServiceEntity service : services) {
            Optional<QueueEntry> servingOpt = queueEntryRepository.findFirstByServiceAndQueueDateAndStatusOrderByJoinTimeAsc(
                    service, today, QueueStatus.SERVING
            );
            String currentlyServingToken = servingOpt.map(QueueEntry::getTokenNumber).orElse("None");
            QueueEntry servingEntry = servingOpt.orElse(null);

            List<QueueEntry> waitingEntries = queueEntryRepository.findActiveEntriesForServiceAndDate(
                    service, today, List.of(QueueStatus.WAITING)
            );

            counters.add(new ServiceCounterResponse(
                    service.getId(),
                    service.getServiceName(),
                    service.getDescription(),
                    service.getAverageServiceTime(),
                    service.isActive(),
                    currentlyServingToken,
                    servingEntry,
                    waitingEntries,
                    waitingEntries.size()
            ));
        }

        return counters;
    }



    public QueueResponse mapToQueueResponse(QueueEntry entry) {
        LocalDate today = entry.getQueueDate();
        ServiceEntity service = entry.getService();

        // Calculate current serving token for this service
        Optional<QueueEntry> servingEntry = queueEntryRepository.findFirstByServiceAndQueueDateAndStatusOrderByJoinTimeAsc(
                service, today, QueueStatus.SERVING
        );
        String currentlyServingToken = servingEntry.map(QueueEntry::getTokenNumber).orElse("None");

        // Calculate people ahead
        long peopleAhead = 0;
        if (entry.getStatus() == QueueStatus.WAITING) {
            peopleAhead = queueEntryRepository.countPeopleAhead(service, today, entry.getJoinTime());
        }

        // Estimated wait time in minutes
        int estimatedWaitTime = (int) (peopleAhead * service.getAverageServiceTime());

        return new QueueResponse(
                entry.getId(),
                entry.getTokenNumber(),
                service.getServiceName(),
                entry.getStatus(),
                currentlyServingToken,
                peopleAhead,
                estimatedWaitTime,
                entry.getJoinTime()
        );
    }

    public Map<String, Object> getPublicQueueDisplayData() {
        LocalDate today = LocalDate.now();
        List<ServiceEntity> activeServices = serviceRepository.findByActiveTrue();

        List<Map<String, Object>> serviceDisplays = new ArrayList<>();

        for (ServiceEntity service : activeServices) {
            Optional<QueueEntry> serving = queueEntryRepository.findFirstByServiceAndQueueDateAndStatusOrderByJoinTimeAsc(
                    service, today, QueueStatus.SERVING
            );
            String servingToken = serving.map(QueueEntry::getTokenNumber).orElse("---");

            List<QueueEntry> waitingEntries = queueEntryRepository.findActiveEntriesForServiceAndDate(
                    service, today, List.of(QueueStatus.WAITING)
            );

            List<String> nextTokens = waitingEntries.stream()
                    .limit(5)
                    .map(QueueEntry::getTokenNumber)
                    .collect(Collectors.toList());

            Map<String, Object> serviceMap = new HashMap<>();
            serviceMap.put("serviceName", service.getServiceName());
            serviceMap.put("nowServing", servingToken);
            serviceMap.put("nextTokens", nextTokens);
            serviceDisplays.add(serviceMap);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("services", serviceDisplays);

        // Also overall top serving
        List<QueueEntry> allServing = queueEntryRepository.findActiveEntriesForDate(today, List.of(QueueStatus.SERVING));
        String mainServing = allServing.isEmpty() ? "---" : allServing.get(0).getTokenNumber();
        result.put("mainServing", mainServing);

        List<QueueEntry> allWaiting = queueEntryRepository.findActiveEntriesForDate(today, List.of(QueueStatus.WAITING));
        List<String> mainNextTokens = allWaiting.stream()
                .limit(5)
                .map(QueueEntry::getTokenNumber)
                .collect(Collectors.toList());
        result.put("mainNextTokens", mainNextTokens);

        return result;
    }

    public Map<String, Object> getAdminDashboardStats() {
        LocalDate today = LocalDate.now();

        long totalUsers = userRepository.count();
        long totalServices = serviceRepository.count();
        long waitingCount = queueEntryRepository.countByQueueDateAndStatus(today, QueueStatus.WAITING);
        long servingCount = queueEntryRepository.countByQueueDateAndStatus(today, QueueStatus.SERVING);
        long completedCount = queueEntryRepository.countByQueueDateAndStatus(today, QueueStatus.COMPLETED);
        long cancelledCount = queueEntryRepository.countByQueueDateAndStatus(today, QueueStatus.CANCELLED);

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", totalUsers);
        stats.put("totalServices", totalServices);
        stats.put("waitingCount", waitingCount);
        stats.put("servingCount", servingCount);
        stats.put("completedCount", completedCount);
        stats.put("cancelledCount", cancelledCount);

        List<QueueEntry> todayEntries = queueEntryRepository.findByQueueDateOrderByJoinTimeAsc(today);
        stats.put("todayEntries", todayEntries);

        return stats;
    }

    public List<QueueEntry> getTodayQueueEntries() {
        return queueEntryRepository.findByQueueDateOrderByJoinTimeAsc(LocalDate.now());
    }

    private String generateServicePrefix(String serviceName) {
        if (serviceName == null || serviceName.isBlank()) {
            return "SER";
        }
        String clean = serviceName.replaceAll("[^a-zA-Z]", "").toUpperCase();
        if (clean.length() >= 3) {
            return clean.substring(0, 3);
        } else {
            return (clean + "XXX").substring(0, 3);
        }
    }
}
