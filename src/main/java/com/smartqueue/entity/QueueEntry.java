package com.smartqueue.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "queue_entries")
public class QueueEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String tokenNumber;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "service_id", nullable = false)
    private ServiceEntity service;

    @Column(nullable = false)
    private LocalDate queueDate;

    @Column(nullable = false)
    private LocalDateTime joinTime;

    private LocalDateTime calledTime;

    private LocalDateTime completedTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QueueStatus status;

    public QueueEntry() {
    }

    public QueueEntry(String tokenNumber, User user, ServiceEntity service, LocalDate queueDate, QueueStatus status) {
        this.tokenNumber = tokenNumber;
        this.user = user;
        this.service = service;
        this.queueDate = queueDate;
        this.joinTime = LocalDateTime.now();
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTokenNumber() {
        return tokenNumber;
    }

    public void setTokenNumber(String tokenNumber) {
        this.tokenNumber = tokenNumber;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ServiceEntity getService() {
        return service;
    }

    public void setService(ServiceEntity service) {
        this.service = service;
    }

    public LocalDate getQueueDate() {
        return queueDate;
    }

    public void setQueueDate(LocalDate queueDate) {
        this.queueDate = queueDate;
    }

    public LocalDateTime getJoinTime() {
        return joinTime;
    }

    public void setJoinTime(LocalDateTime joinTime) {
        this.joinTime = joinTime;
    }

    public LocalDateTime getCalledTime() {
        return calledTime;
    }

    public void setCalledTime(LocalDateTime calledTime) {
        this.calledTime = calledTime;
    }

    public LocalDateTime getCompletedTime() {
        return completedTime;
    }

    public void setCompletedTime(LocalDateTime completedTime) {
        this.completedTime = completedTime;
    }

    public QueueStatus getStatus() {
        return status;
    }

    public void setStatus(QueueStatus status) {
        this.status = status;
    }
}
