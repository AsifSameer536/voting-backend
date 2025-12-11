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

    @Transactional(readOnly = true)
    public List<Election> findAll() {
        return electionRepository.findAll();
    }

    @Transactional(readOnly = true)
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
        e.setCreatedAt(Instant.now());
        // default state if not provided: UPCOMING
        e.setState("UPCOMING");
        // IMPORTANT: set isActive to false by default (avoids DB NOT NULL issue)
        e.setActive(false);
        return electionRepository.save(e);
    }

    @Transactional
    public Election update(Long id, String title, String description, Instant startAt, Instant endAt, String state) {
        Optional<Election> opt = electionRepository.findById(id);
        if (opt.isEmpty()) return null;
        Election e = opt.get();
        if (title != null) e.setTitle(title);
        if (description != null) e.setDescription(description);
        if (startAt != null) e.setStartAt(startAt);
        if (endAt != null) e.setEndAt(endAt);
        if (state != null) e.setState(state);
        return electionRepository.save(e);
    }

    @Transactional
    public void deleteById(Long id) {
        electionRepository.deleteById(id);
    }

    public boolean isActive(Election e) {
        if (e == null) return false;
        if ("ACTIVE".equalsIgnoreCase(e.getState())) return true;
        Instant now = Instant.now();
        if (e.getStartAt() != null && e.getEndAt() != null) {
            return now.isAfter(e.getStartAt()) && now.isBefore(e.getEndAt());
        }
        return e.isActive();
    }
}
