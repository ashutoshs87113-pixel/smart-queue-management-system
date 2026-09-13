package com.smartqueue.service;

import com.smartqueue.dto.QueueResponse;
import com.smartqueue.dto.ServiceCounterResponse;
import com.smartqueue.entity.QueueEntry;
import com.smartqueue.entity.QueueStatus;
import com.smartqueue.entity.Role;
import com.smartqueue.entity.ServiceEntity;
import com.smartqueue.entity.User;
import com.smartqueue.exception.QueueException;
import com.smartqueue.repository.QueueEntryRepository;
import com.smartqueue.repository.ServiceRepository;
import com.smartqueue.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QueueServiceTest {

    @Mock
    private QueueEntryRepository queueEntryRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ServiceRepository serviceRepository;

    @InjectMocks
    private QueueService queueService;

    private User sampleUser;
    private ServiceEntity sampleService;

    @BeforeEach
    void setUp() {
        sampleUser = new User("Ashutosh Kumar", "ashutosh123", "ashutosh@gmail.com", "pass", "9876543210", Role.USER);
        sampleUser.setId(1L);

        sampleService = new ServiceEntity("Admission", "Admission details", 10, true);
        sampleService.setId(1L);
    }

    @Test
    void joinQueue_Success() {
        when(userRepository.findByUsername("ashutosh123")).thenReturn(Optional.of(sampleUser));
        when(serviceRepository.findById(1L)).thenReturn(Optional.of(sampleService));
        when(queueEntryRepository.findFirstByUserAndServiceAndQueueDateAndStatusIn(any(), any(), any(), any()))
                .thenReturn(Optional.empty());
        when(queueEntryRepository.countByServiceAndQueueDate(any(), any())).thenReturn(0L);
        when(queueEntryRepository.save(any(QueueEntry.class))).thenAnswer(i -> i.getArgument(0));

        QueueResponse response = queueService.joinQueue("ashutosh123", 1L);

        assertNotNull(response);
        assertEquals("ADM-001", response.getTokenNumber());
        assertEquals("Admission", response.getServiceName());
        assertEquals(QueueStatus.WAITING, response.getStatus());

        verify(queueEntryRepository, times(1)).save(any(QueueEntry.class));
    }

    @Test
    void joinQueue_AlreadyInQueue_ThrowsQueueException() {
        QueueEntry activeEntry = new QueueEntry("ADM-001", sampleUser, sampleService, LocalDate.now(), QueueStatus.WAITING);

        when(userRepository.findByUsername("ashutosh123")).thenReturn(Optional.of(sampleUser));
        when(serviceRepository.findById(1L)).thenReturn(Optional.of(sampleService));
        when(queueEntryRepository.findFirstByUserAndServiceAndQueueDateAndStatusIn(any(), any(), any(), any()))
                .thenReturn(Optional.of(activeEntry));

        QueueException exception = assertThrows(QueueException.class,
                () -> queueService.joinQueue("ashutosh123", 1L));

        assertEquals("You are already in this queue.", exception.getMessage());
        verify(queueEntryRepository, never()).save(any());
    }

    @Test
    void callNextToken_Success() {
        QueueEntry waitingEntry = new QueueEntry("ADM-003", sampleUser, sampleService, LocalDate.now(), QueueStatus.WAITING);

        when(serviceRepository.findById(1L)).thenReturn(Optional.of(sampleService));
        when(queueEntryRepository.findFirstByServiceAndQueueDateAndStatusOrderByJoinTimeAsc(eq(sampleService), any(LocalDate.class), eq(QueueStatus.SERVING)))
                .thenReturn(Optional.empty());
        when(queueEntryRepository.findFirstByServiceAndQueueDateAndStatusOrderByJoinTimeAsc(eq(sampleService), any(LocalDate.class), eq(QueueStatus.WAITING)))
                .thenReturn(Optional.of(waitingEntry));
        when(queueEntryRepository.save(any(QueueEntry.class))).thenAnswer(i -> i.getArgument(0));

        QueueResponse response = queueService.callNextToken(1L);

        assertNotNull(response);
        assertEquals("ADM-003", response.getTokenNumber());
        assertEquals(QueueStatus.SERVING, response.getStatus());
    }

    @Test
    void callNextToken_ServingExists_ThrowsException() {
        QueueEntry servingEntry = new QueueEntry("ADM-002", sampleUser, sampleService, LocalDate.now(), QueueStatus.SERVING);

        when(serviceRepository.findById(1L)).thenReturn(Optional.of(sampleService));
        when(queueEntryRepository.findFirstByServiceAndQueueDateAndStatusOrderByJoinTimeAsc(eq(sampleService), any(LocalDate.class), eq(QueueStatus.SERVING)))
                .thenReturn(Optional.of(servingEntry));

        QueueException exception = assertThrows(QueueException.class, () -> queueService.callNextToken(1L));
        assertTrue(exception.getMessage().contains("is currently SERVING"));
    }

    @Test
    void completeCurrentToken_Success() {
        QueueEntry servingEntry = new QueueEntry("ADM-002", sampleUser, sampleService, LocalDate.now(), QueueStatus.SERVING);

        when(serviceRepository.findById(1L)).thenReturn(Optional.of(sampleService));
        when(queueEntryRepository.findFirstByServiceAndQueueDateAndStatusOrderByJoinTimeAsc(eq(sampleService), any(LocalDate.class), eq(QueueStatus.SERVING)))
                .thenReturn(Optional.of(servingEntry));
        when(queueEntryRepository.save(any(QueueEntry.class))).thenAnswer(i -> i.getArgument(0));

        QueueResponse response = queueService.completeCurrentToken(1L);

        assertNotNull(response);
        assertEquals("ADM-002", response.getTokenNumber());
        assertEquals(QueueStatus.COMPLETED, response.getStatus());
    }

    @Test
    void skipCurrentToken_Success() {
        QueueEntry servingEntry = new QueueEntry("ADM-002", sampleUser, sampleService, LocalDate.now(), QueueStatus.SERVING);

        when(serviceRepository.findById(1L)).thenReturn(Optional.of(sampleService));
        when(queueEntryRepository.findFirstByServiceAndQueueDateAndStatusOrderByJoinTimeAsc(eq(sampleService), any(LocalDate.class), eq(QueueStatus.SERVING)))
                .thenReturn(Optional.of(servingEntry));
        when(queueEntryRepository.save(any(QueueEntry.class))).thenAnswer(i -> i.getArgument(0));

        QueueResponse response = queueService.skipCurrentToken(1L);

        assertNotNull(response);
        assertEquals("ADM-002", response.getTokenNumber());
        assertEquals(QueueStatus.SKIPPED, response.getStatus());
    }

    @Test
    void getServiceCountersData_Success() {
        when(serviceRepository.findAll()).thenReturn(List.of(sampleService));
        when(queueEntryRepository.findFirstByServiceAndQueueDateAndStatusOrderByJoinTimeAsc(eq(sampleService), any(LocalDate.class), eq(QueueStatus.SERVING)))
                .thenReturn(Optional.empty());
        when(queueEntryRepository.findActiveEntriesForServiceAndDate(eq(sampleService), any(LocalDate.class), any()))
                .thenReturn(List.of(new QueueEntry("ADM-003", sampleUser, sampleService, LocalDate.now(), QueueStatus.WAITING)));

        List<ServiceCounterResponse> counters = queueService.getServiceCountersData();

        assertNotNull(counters);
        assertEquals(1, counters.size());
        assertEquals("Admission", counters.get(0).getServiceName());
        assertEquals("None", counters.get(0).getCurrentlyServingToken());
        assertEquals(1, counters.get(0).getWaitingCount());
        assertEquals("ADM-003", counters.get(0).getWaitingEntries().get(0).getTokenNumber());
    }
}
