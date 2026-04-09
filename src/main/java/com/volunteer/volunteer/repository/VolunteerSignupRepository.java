package com.volunteer.volunteer.repository;

import com.volunteer.volunteer.model.VolunteerSignup;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface VolunteerSignupRepository extends JpaRepository<VolunteerSignup, Long> {
    List<VolunteerSignup> findByUserId(Long userId);
    List<VolunteerSignup> findByEventId(Long eventId);
    List<VolunteerSignup> findByEventIdAndStatus(Long eventId, String status);
    Optional<VolunteerSignup> findByUserIdAndEventId(Long userId, Long eventId);
    int countByEventId(Long eventId);
    int countByEventIdAndStatus(Long id, String string);
}