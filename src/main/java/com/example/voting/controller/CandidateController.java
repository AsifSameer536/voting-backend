package com.example.voting.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.voting.dto.CandidateResponse;
import com.example.voting.dto.CreateCandidateRequest;
import com.example.voting.dto.UpdateCandidateRequest;
import com.example.voting.model.Candidate;
import com.example.voting.service.CandidateService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/candidates")
@Validated
public class CandidateController {

    private final CandidateService candidateService;

    public CandidateController(CandidateService candidateService) {
        this.candidateService = candidateService;
    }

    // Create candidate - ADMIN only
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> create(@Valid @RequestBody CreateCandidateRequest req) {
        Candidate created = candidateService.create(req.getElectionId(), req.getName(), req.getManifesto());
        return ResponseEntity.status(201).body(toDto(created));
    }

    // List all candidates - any authenticated user
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<CandidateResponse>> list() {
        List<CandidateResponse> out = candidateService.findAll().stream()
                .map(this::toDto).collect(Collectors.toList());
        return ResponseEntity.ok(out);
    }

    // Get by id
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        Optional<Candidate> opt = candidateService.findById(id);
        if (opt.isPresent()) {
            Candidate c = opt.get();
            return ResponseEntity.ok(toDto(c));
        } else {
            return ResponseEntity.status(404).body("candidate not found");
        }
    }

    // Update - ADMIN only
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> update(@PathVariable Long id,
                                    @Valid @RequestBody UpdateCandidateRequest req) {
        try {
            Candidate updated = candidateService.update(id, req.getElectionId(), req.getName(), req.getManifesto());
            return ResponseEntity.ok(toDto(updated));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    // Delete - ADMIN only
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        candidateService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private CandidateResponse toDto(Candidate c) {
        CandidateResponse r = new CandidateResponse();
        r.setId(c.getId());
        r.setElectionId(c.getElection() != null ? c.getElection().getId() : null);
        r.setName(c.getName());
        r.setManifesto(c.getManifesto());
        r.setCreatedAt(c.getCreatedAt());
        return r;
    }
}
