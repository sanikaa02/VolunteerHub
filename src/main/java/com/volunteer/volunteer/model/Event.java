package com.volunteer.volunteer.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    private String location;
    private String eventDate;
    private String startTime;
    private String endTime;
    private Integer maxVolunteers;
    private String category;
    private String status = "UPCOMING";
    private Long createdBy;
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() { this.createdAt = LocalDateTime.now(); }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getLocation() { return location; }
    public String getEventDate() { return eventDate; }
    public String getStartTime() { return startTime; }
    public String getEndTime() { return endTime; }
    public Integer getMaxVolunteers() { return maxVolunteers; }
    public String getCategory() { return category; }
    public String getStatus() { return status; }
    public Long getCreatedBy() { return createdBy; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setId(Long id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setLocation(String location) { this.location = location; }
    public void setEventDate(String eventDate) { this.eventDate = eventDate; }
    public void setStartTime(String startTime) { this.startTime = startTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }
    public void setMaxVolunteers(Integer maxVolunteers) { this.maxVolunteers = maxVolunteers; }
    public void setCategory(String category) { this.category = category; }
    public void setStatus(String status) { this.status = status; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }
}