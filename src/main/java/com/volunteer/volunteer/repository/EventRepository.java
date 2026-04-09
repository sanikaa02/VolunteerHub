package com.volunteer.volunteer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.volunteer.volunteer.model.Event;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByStatus(String status);
    List<Event> findByCategory(String category);
    List<Event> findByCreatedBy(Long createdBy);
}