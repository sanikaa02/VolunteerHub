package com.volunteer.volunteer.controller;

import com.volunteer.volunteer.model.User;
import com.volunteer.volunteer.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String password = body.get("password");
        Optional<User> user = userRepository.findByEmailAndPassword(email, password);
        if (user.isPresent()) {
            return ResponseEntity.ok(Map.of(
                "message", "Login successful",
                "userId", user.get().getId(),
                "name", user.get().getName(),
                "role", user.get().getRole()
            ));
        }
        return ResponseEntity.status(401).body(Map.of("message", "Invalid credentials"));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        if (user.getRole() == null) user.setRole("VOLUNTEER");
        userRepository.save(user);
        return ResponseEntity.ok(Map.of("message", "Registered successfully"));
    }

    @GetMapping("/search")
    public ResponseEntity<?> search(@RequestParam String name) {
        var users = userRepository.findByNameContainingIgnoreCase(name);
        if (users.isEmpty())
            return ResponseEntity.status(404).body(Map.of("message", "No user found"));
        return ResponseEntity.ok(users.stream().map(u ->
            Map.of("id", u.getId(), "name", u.getName(), "email", u.getEmail(), "role", u.getRole())
        ).toList());
    }
}