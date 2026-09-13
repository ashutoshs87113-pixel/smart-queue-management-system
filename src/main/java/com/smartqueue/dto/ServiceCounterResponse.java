package com.smartqueue.dto;

import com.smartqueue.entity.QueueEntry;

import java.util.List;

public class ServiceCounterResponse {

    private Long serviceId;
    private String serviceName;
    private String description;
    private Integer averageServiceTime;
    private boolean active;
    private String currentlyServingToken;
    private QueueEntry servingEntry;
    private List<QueueEntry> waitingEntries;
    private long waitingCount;

    public ServiceCounterResponse() {
    }

    public ServiceCounterResponse(Long serviceId, String serviceName, String description, Integer averageServiceTime,
                                  boolean active, String currentlyServingToken, QueueEntry servingEntry,
                                  List<QueueEntry> waitingEntries, long waitingCount) {
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.description = description;
        this.averageServiceTime = averageServiceTime;
        this.active = active;
        this.currentlyServingToken = currentlyServingToken;
        this.servingEntry = servingEntry;
        this.waitingEntries = waitingEntries;
        this.waitingCount = waitingCount;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getAverageServiceTime() {
        return averageServiceTime;
    }

    public void setAverageServiceTime(Integer averageServiceTime) {
        this.averageServiceTime = averageServiceTime;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getCurrentlyServingToken() {
        return currentlyServingToken;
    }

    public void setCurrentlyServingToken(String currentlyServingToken) {
        this.currentlyServingToken = currentlyServingToken;
    }

    public QueueEntry getServingEntry() {
        return servingEntry;
    }

    public void setServingEntry(QueueEntry servingEntry) {
        this.servingEntry = servingEntry;
    }

    public List<QueueEntry> getWaitingEntries() {
        return waitingEntries;
    }

    public void setWaitingEntries(List<QueueEntry> waitingEntries) {
        this.waitingEntries = waitingEntries;
    }

    public long getWaitingCount() {
        return waitingCount;
    }

    public void setWaitingCount(long waitingCount) {
        this.waitingCount = waitingCount;
    }
}
