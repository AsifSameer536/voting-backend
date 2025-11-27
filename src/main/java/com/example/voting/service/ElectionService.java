package com.example.voting.service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.voting.model.Election;
import com.example.voting.repository.ElectionRepository;

@Service
public class ElectionService {

    private final ElectionRepository electionRepository;

    public ElectionService(ElectionRepository electionRepository) {
        this.electionRepository = electionRepository;
    }

    public List<Election> findAll() {
        return electionRepository.findAll();
    }

    public Optional<Election> findById(Long id) {
        return electionRepository.findById(id);
    }

    @Transactional
    public Election create(String title, String description, Instant startAt, Instant endAt) {
        Election e = new Election();
        e.setTitle(title);
        e.setDescription(description);
        e.setStartAt(startAt);
        e.setEndAt(endAt);
        e.setState("UPCOMING");
        return electionRepository.save(e);
    }

    @Transactional
    public Election update(Long id, String title, String description, Instant startAt, Instant endAt, String state) {
        Election e = electionRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Election not found"));
        if (title != null) e.setTitle(title);
        if (description != null) e.setDescription(description);
        if (startAt != null) e.setStartAt(startAt);
        if (endAt != null) e.setEndAt(endAt);
        if (state != null) {
            if (!state.equals("UPCOMING") && !state.equals("ACTIVE") && !state.equals("CLOSED")) {
                throw new IllegalArgumentException("Invalid state");
            }
            e.setState(state);
        }
        return electionRepository.save(e);
    }

    public boolean isActive(Election e) {
        if (e == null) return false;
        // If state says ACTIVE, honor it. Optionally check schedule.
        if ("ACTIVE".equals(e.getState())) return true;
        // If state is UPCOMING but current time inside start/end, optionally auto-activate (don't auto-change DB)
        Instant now = Instant.now();
        if (e.getStartAt() != null && e.getEndAt() != null) {
            return now.isAfter(e.getStartAt()) && now.isBefore(e.getEndAt());
        }
        return false;
    }
}
