package com.volunteer.volunteer.controller;

import com.volunteer.volunteer.model.*;
import com.volunteer.volunteer.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class EventController {

    @Autowired EventRepository eventRepository;
    @Autowired VolunteerSignupRepository signupRepository;
    @Autowired UserRepository userRepository;

    @GetMapping("/events")
    public List<Map<String, Object>> getAllEvents() {
        List<Event> events = eventRepository.findAll();
        List<Map<String, Object>> result = new ArrayList<>();
        for (Event e : events) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", e.getId());
            map.put("title", e.getTitle());
            map.put("description", e.getDescription());
            map.put("location", e.getLocation());
            map.put("eventDate", e.getEventDate());
            map.put("startTime", e.getStartTime());
            map.put("endTime", e.getEndTime());
            map.put("maxVolunteers", e.getMaxVolunteers());
            map.put("category", e.getCategory());
            map.put("status", e.getStatus());
            map.put("createdBy", e.getCreatedBy());
            // accepted count = filled slots
            int accepted = signupRepository.countByEventIdAndStatus(e.getId(), "ACCEPTED");
            int pending = signupRepository.countByEventIdAndStatus(e.getId(), "PENDING");
            map.put("acceptedCount", accepted);
            map.put("pendingCount", pending);
            map.put("isFull", accepted >= e.getMaxVolunteers());
            result.add(map);
        }
        return result;
    }

    @GetMapping("/events/{id}")
    public ResponseEntity<?> getEvent(@PathVariable Long id) {
        return eventRepository.findById(id)
            .map(e -> ResponseEntity.ok(e))
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/events")
    public ResponseEntity<?> createEvent(@RequestBody Event event) {
        eventRepository.save(event);
        return ResponseEntity.ok(Map.of("message", "Event created successfully"));
    }

    @PutMapping("/events/{id}")
    public ResponseEntity<?> updateEvent(@PathVariable Long id, @RequestBody Event updatedEvent) {
        return eventRepository.findById(id).map(event -> {
            if (updatedEvent.getTitle() != null) event.setTitle(updatedEvent.getTitle());
            if (updatedEvent.getDescription() != null) event.setDescription(updatedEvent.getDescription());
            if (updatedEvent.getLocation() != null) event.setLocation(updatedEvent.getLocation());
            if (updatedEvent.getEventDate() != null) event.setEventDate(updatedEvent.getEventDate());
            if (updatedEvent.getStartTime() != null) event.setStartTime(updatedEvent.getStartTime());
            if (updatedEvent.getEndTime() != null) event.setEndTime(updatedEvent.getEndTime());
            if (updatedEvent.getMaxVolunteers() != null) event.setMaxVolunteers(updatedEvent.getMaxVolunteers());
            if (updatedEvent.getCategory() != null) event.setCategory(updatedEvent.getCategory());
            if (updatedEvent.getStatus() != null) event.setStatus(updatedEvent.getStatus());
            eventRepository.save(event);
            return ResponseEntity.ok(Map.of("message", "Event updated successfully"));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/events/{id}")
    public ResponseEntity<?> deleteEvent(@PathVariable Long id) {
        if (!eventRepository.existsById(id)) return ResponseEntity.notFound().build();
        signupRepository.findByEventId(id).forEach(signupRepository::delete);
        eventRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Event deleted successfully"));
    }

    // Volunteer signup — goes to PENDING first
    @PostMapping("/volunteer/signup")
    public ResponseEntity<?> signup(@RequestBody Map<String, Long> body) {
        Long userId = body.get("userId");
        Long eventId = body.get("eventId");

        if (signupRepository.findByUserIdAndEventId(userId, eventId).isPresent())
            return ResponseEntity.badRequest().body(Map.of("message", "Already requested to join this event"));

        Event event = eventRepository.findById(eventId).orElse(null);
        if (event == null)
            return ResponseEntity.badRequest().body(Map.of("message", "Event not found"));

        // Check if accepted slots are full
        int accepted = signupRepository.countByEventIdAndStatus(eventId, "ACCEPTED");
        if (accepted >= event.getMaxVolunteers())
            return ResponseEntity.badRequest().body(Map.of("message", "Event is full. No more slots available."));

        VolunteerSignup signup = new VolunteerSignup();
        signup.setUserId(userId);
        signup.setEventId(eventId);
        signup.setStatus("PENDING");
        signupRepository.save(signup);
        return ResponseEntity.ok(Map.of("message", "Request sent! Waiting for admin approval."));
    }

    // Admin: Accept or Reject a volunteer
    @PutMapping("/volunteer/review")
    public ResponseEntity<?> reviewSignup(@RequestBody Map<String, Object> body) {
        Long userId = Long.valueOf(body.get("userId").toString());
        Long eventId = Long.valueOf(body.get("eventId").toString());
        String action = body.get("action").toString(); // ACCEPTED or REJECTED

        var signup = signupRepository.findByUserIdAndEventId(userId, eventId);
        if (signup.isEmpty())
            return ResponseEntity.badRequest().body(Map.of("message", "Signup not found"));

        if (action.equals("ACCEPTED")) {
            // Check slots again before accepting
            Event event = eventRepository.findById(eventId).orElse(null);
            if (event != null) {
                int accepted = signupRepository.countByEventIdAndStatus(eventId, "ACCEPTED");
                if (accepted >= event.getMaxVolunteers())
                    return ResponseEntity.badRequest().body(Map.of("message", "Cannot accept — event is already full"));
            }
        }

        signup.get().setStatus(action);
        signupRepository.save(signup.get());
        return ResponseEntity.ok(Map.of("message", "Volunteer " + action.toLowerCase()));
    }

    @DeleteMapping("/volunteer/cancel")
    public ResponseEntity<?> cancel(@RequestBody Map<String, Long> body) {
        Long userId = body.get("userId");
        Long eventId = body.get("eventId");
        var signup = signupRepository.findByUserIdAndEventId(userId, eventId);
        if (signup.isEmpty())
            return ResponseEntity.badRequest().body(Map.of("message", "Not signed up"));
        signupRepository.delete(signup.get());
        return ResponseEntity.ok(Map.of("message", "Cancelled successfully"));
    }

    @GetMapping("/volunteer/my-events/{userId}")
    public ResponseEntity<?> myEvents(@PathVariable Long userId) {
        var signups = signupRepository.findByUserId(userId);
        List<Map<String, Object>> result = new ArrayList<>();
        for (var s : signups) {
            var event = eventRepository.findById(s.getEventId()).orElse(null);
            if (event == null) continue;
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", event.getId());
            map.put("title", event.getTitle());
            map.put("description", event.getDescription());
            map.put("location", event.getLocation());
            map.put("eventDate", event.getEventDate());
            map.put("startTime", event.getStartTime());
            map.put("endTime", event.getEndTime());
            map.put("maxVolunteers", event.getMaxVolunteers());
            map.put("category", event.getCategory());
            map.put("status", event.getStatus());
            map.put("signupStatus", s.getStatus()); // PENDING / ACCEPTED / REJECTED
            result.add(map);
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/volunteer/event/{eventId}")
    public ResponseEntity<?> getVolunteers(@PathVariable Long eventId) {
        var signups = signupRepository.findByEventId(eventId);
        var volunteers = signups.stream().map(s -> {
            var user = userRepository.findById(s.getUserId()).orElse(null);
            if (user == null) return null;
            return Map.of(
                "id", user.getId(),
                "name", user.getName(),
                "email", user.getEmail(),
                "phone", user.getPhone() != null ? user.getPhone() : "N/A",
                "signedUpAt", s.getSignedUpAt() != null ? s.getSignedUpAt().toString() : "",
                "signupStatus", s.getStatus()
            );
        }).filter(v -> v != null).toList();
        return ResponseEntity.ok(volunteers);
    }

    @GetMapping("/stats")
    public ResponseEntity<?> stats() {
        long totalEvents = eventRepository.count();
        long totalSignups = signupRepository.count();
        long upcomingEvents = eventRepository.findByStatus("UPCOMING").size();
        return ResponseEntity.ok(Map.of(
            "totalEvents", totalEvents,
            "totalSignups", totalSignups,
            "upcomingEvents", upcomingEvents
        ));
    }
}