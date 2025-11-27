package com.example.voting.controller;

import com.example.voting.dto.CreateElectionRequest;
import com.example.voting.dto.UpdateElectionRequest;
import com.example.voting.model.Election;
import com.example.voting.service.ElectionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/elections")
@Validated
public class ElectionController {

    private final ElectionService electionService;

    public ElectionController(ElectionService electionService) {
        this.electionService = electionService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> create(@Valid @RequestBody CreateElectionRequest req) {
        Election e = electionService.create(req.getTitle(), req.getDescription(), req.getStartAt(), req.getEndAt());
        return ResponseEntity.status(201).body(toMap(e));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> list() {
        List<Map<String, Object>> out = electionService.findAll().stream().map(this::toMap).collect(Collectors.toList());
        return ResponseEntity.ok(out);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> get(@PathVariable Long id) {
        return electionService.findById(id)
                .map(e -> ResponseEntity.ok(toMap(e)))
                .orElseGet(() -> ResponseEntity.status(404).body("election not found"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody UpdateElectionRequest req) {
        try {
            Election updated = electionService.update(id, req.getTitle(), req.getDescription(), req.getStartAt(), req.getEndAt(), req.getState());
            return ResponseEntity.ok(toMap(updated));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(404).body(ex.getMessage());
        }
    }

    @PostMapping("/{id}/state")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> changeState(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String newState = body.get("state");
        if (newState == null) return ResponseEntity.badRequest().body("state required");
        try {
            Election updated = electionService.update(id, null, null, null, null, newState);
            return ResponseEntity.ok(toMap(updated));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(400).body(ex.getMessage());
        }
    }

    private Map<String,Object> toMap(Election e) {
        return Map.of(
                "id", e.getId(),
                "title", e.getTitle(),
                "description", e.getDescription(),
                "state", e.getState(),
                "startAt", e.getStartAt(),
                "endAt", e.getEndAt(),
                "createdAt", e.getCreatedAt()
        );
    }
}
