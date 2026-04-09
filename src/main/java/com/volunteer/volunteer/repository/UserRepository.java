package com.volunteer.volunteer.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.volunteer.volunteer.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailAndPassword(String email, String password);
    List<User> findByNameContainingIgnoreCase(String name);
}