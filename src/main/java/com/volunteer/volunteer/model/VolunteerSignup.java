package com.volunteer.volunteer.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "volunteer_signups")
public class VolunteerSignup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private Long eventId;
    private String status = "PENDING";
    private LocalDateTime signedUpAt;

    @PrePersist
    public void prePersist() { this.signedUpAt = LocalDateTime.now(); }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public Long getEventId() { return eventId; }
    public String getStatus() { return status; }
    public LocalDateTime getSignedUpAt() { return signedUpAt; }
    public void setUserId(Long userId) { this.userId = userId; }
    public void setEventId(Long eventId) { this.eventId = eventId; }
    public void setStatus(String status) { this.status = status; }
}