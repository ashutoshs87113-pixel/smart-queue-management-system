package com.smartqueue.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "services")
public class ServiceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String serviceName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Integer averageServiceTime; // in minutes

    @Column(nullable = false)
    private boolean active = true;

    public ServiceEntity() {
    }

    public ServiceEntity(String serviceName, String description, Integer averageServiceTime, boolean active) {
        this.serviceName = serviceName;
        this.description = description;
        this.averageServiceTime = averageServiceTime;
        this.active = active;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
}
