package com.smartqueue.dto;

import com.smartqueue.entity.QueueStatus;

import java.time.LocalDateTime;

public class QueueResponse {

    private Long queueId;
    private String tokenNumber;
    private String serviceName;
    private QueueStatus status;
    private String currentlyServingToken;
    private long peopleAhead;
    private int estimatedWaitTimeMinutes;
    private LocalDateTime joinTime;

    public QueueResponse() {
    }

    public QueueResponse(Long queueId, String tokenNumber, String serviceName, QueueStatus status,
                         String currentlyServingToken, long peopleAhead, int estimatedWaitTimeMinutes,
                         LocalDateTime joinTime) {
        this.queueId = queueId;
        this.tokenNumber = tokenNumber;
        this.serviceName = serviceName;
        this.status = status;
        this.currentlyServingToken = currentlyServingToken;
        this.peopleAhead = peopleAhead;
        this.estimatedWaitTimeMinutes = estimatedWaitTimeMinutes;
        this.joinTime = joinTime;
    }

    public Long getQueueId() {
        return queueId;
    }

    public void setQueueId(Long queueId) {
        this.queueId = queueId;
    }

    public String getTokenNumber() {
        return tokenNumber;
    }

    public void setTokenNumber(String tokenNumber) {
        this.tokenNumber = tokenNumber;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public QueueStatus getStatus() {
        return status;
    }

    public void setStatus(QueueStatus status) {
        this.status = status;
    }

    public String getCurrentlyServingToken() {
        return currentlyServingToken;
    }

    public void setCurrentlyServingToken(String currentlyServingToken) {
        this.currentlyServingToken = currentlyServingToken;
    }

    public long getPeopleAhead() {
        return peopleAhead;
    }

    public void setPeopleAhead(long peopleAhead) {
        this.peopleAhead = peopleAhead;
    }

    public int getEstimatedWaitTimeMinutes() {
        return estimatedWaitTimeMinutes;
    }

    public void setEstimatedWaitTimeMinutes(int estimatedWaitTimeMinutes) {
        this.estimatedWaitTimeMinutes = estimatedWaitTimeMinutes;
    }

    public LocalDateTime getJoinTime() {
        return joinTime;
    }

    public void setJoinTime(LocalDateTime joinTime) {
        this.joinTime = joinTime;
    }
}
