package com.example.voting.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.voting.model.Voter;

public interface VoterRepository extends JpaRepository<Voter, Long> {
    Optional<Voter> findByUsername(String username);
    boolean existsByUsername(String username);
}
