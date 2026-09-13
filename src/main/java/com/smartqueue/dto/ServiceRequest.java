package com.smartqueue.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ServiceRequest {

    private Long id;

    @NotBlank(message = "Service name is required")
    private String serviceName;

    @NotBlank(message = "Service description is required")
    private String description;

    @NotNull(message = "Average service time is required")
    @Min(value = 1, message = "Average service time must be at least 1 minute")
    private Integer averageServiceTime;

    private boolean active = true;

    public ServiceRequest() {
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
