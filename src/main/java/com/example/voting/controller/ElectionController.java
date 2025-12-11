package com.example.voting.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.voting.dto.ElectionRequest;
import com.example.voting.model.Election;
import com.example.voting.service.ElectionService;

/**
 * REST controller for elections (uses extended DTO).
 */
@RestController
@RequestMapping("/api/elections")
public class ElectionController {

    private final ElectionService electionService;

    public ElectionController(ElectionService electionService) {
        this.electionService = electionService;
    }

    @GetMapping
    public ResponseEntity<List<Election>> list() {
        return ResponseEntity.ok(electionService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Election> getOne(@PathVariable Long id) {
        Optional<Election> opt = electionService.findById(id);
        return opt.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Election> create(@RequestBody ElectionRequest req) {
        Election created = electionService.create(
                req.getTitle(),
                req.getDescription(),
                req.getStartsAt(),
                req.getEndsAt()
        );
        // If caller provided state and you want to honor it, set it explicitly:
        if (req.getState() != null) {
            created.setState(req.getState());
            // persist change
            created = electionService.update(created.getId(),
                    created.getTitle(), created.getDescription(),
                    created.getStartAt(), created.getEndAt(),
                    req.getState());
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Election> update(@PathVariable Long id, @RequestBody ElectionRequest req) {
        Election updated = electionService.update(
                id,
                req.getTitle(),
                req.getDescription(),
                req.getStartsAt(),
                req.getEndsAt(),
                req.getState()
        );
        if (updated == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        Optional<Election> opt = electionService.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        electionService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
