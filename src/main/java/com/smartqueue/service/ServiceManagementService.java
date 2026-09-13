package com.smartqueue.service;

import com.smartqueue.dto.ServiceRequest;
import com.smartqueue.entity.ServiceEntity;
import com.smartqueue.exception.ResourceNotFoundException;
import com.smartqueue.repository.ServiceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ServiceManagementService {

    private final ServiceRepository serviceRepository;

    public ServiceManagementService(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    public List<ServiceEntity> getAllActiveServices() {
        return serviceRepository.findByActiveTrue();
    }

    public List<ServiceEntity> getAllServices() {
        return serviceRepository.findAll();
    }

    public ServiceEntity getServiceById(Long id) {
        return serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with ID: " + id));
    }

    @Transactional
    public ServiceEntity createService(ServiceRequest request) {
        if (serviceRepository.existsByServiceName(request.getServiceName().trim())) {
            throw new IllegalArgumentException("Service with name '" + request.getServiceName() + "' already exists.");
        }

        ServiceEntity service = new ServiceEntity(
                request.getServiceName().trim(),
                request.getDescription().trim(),
                request.getAverageServiceTime(),
                request.isActive()
        );

        return serviceRepository.save(service);
    }

    @Transactional
    public ServiceEntity updateService(Long id, ServiceRequest request) {
        ServiceEntity service = getServiceById(id);

        if (!service.getServiceName().equalsIgnoreCase(request.getServiceName().trim()) &&
                serviceRepository.existsByServiceName(request.getServiceName().trim())) {
            throw new IllegalArgumentException("Service name '" + request.getServiceName() + "' is already taken.");
        }

        service.setServiceName(request.getServiceName().trim());
        service.setDescription(request.getDescription().trim());
        service.setAverageServiceTime(request.getAverageServiceTime());
        service.setActive(request.isActive());

        return serviceRepository.save(service);
    }

    @Transactional
    public ServiceEntity toggleServiceActive(Long id) {
        ServiceEntity service = getServiceById(id);
        service.setActive(!service.isActive());
        return serviceRepository.save(service);
    }

    public long getTotalServicesCount() {
        return serviceRepository.count();
    }
}
