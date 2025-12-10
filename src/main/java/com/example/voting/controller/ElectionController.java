package com.example.voting.controller;

import java.lang.reflect.Method;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.voting.dto.ElectionRequest;
import com.example.voting.model.Election;
import com.example.voting.repository.ElectionRepository;
import com.example.voting.service.ElectionService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for listing and creating elections.
 * - GET /elections  -> list
 * - POST /elections -> create (requires ROLE_ADMIN)
 *
 * Note: ElectionService.create expects (String, String, Instant, Instant).
 * We pass an empty string as the second parameter here; replace with a real field
 * (e.g. description or slug) if your DTO/entity uses one.
 */
@RestController
@RequestMapping("/elections")
public class ElectionController {

    private final ElectionRepository electionRepository;
    private final ElectionService electionService;

    public ElectionController(ElectionRepository electionRepository,
                              ElectionService electionService) {
        this.electionRepository = electionRepository;
        this.electionService = electionService;
    }

    @GetMapping
    public ResponseEntity<?> listElections() {
        List<Election> elections = electionRepository.findAll();
        List<Map<String, Object>> out = new ArrayList<>();

        for (Election e : elections) {
            Map<String, Object> m = new HashMap<>();
            try { m.put("id", invokeIfExists(e, "getId")); } catch (Exception ignored) {}
            try { m.put("title", invokeIfExists(e, "getTitle", "getName")); } catch (Exception ignored) {}
            try { Object starts = invokeIfExists(e, "getStartsAt", "getStart", "getStartTime", "getStartDate"); if (starts != null) m.put("startsAt", starts); } catch (Exception ignored) {}
            try { Object ends = invokeIfExists(e, "getEndsAt", "getEnd", "getEndTime", "getEndDate"); if (ends != null) m.put("endsAt", ends); } catch (Exception ignored) {}

            out.add(m);
        }

        return ResponseEntity.ok(out);
    }

    /**
     * Create new election - requires ROLE_ADMIN.
     * Expects JSON body mapped to ElectionRequest (title, startsAt, endsAt).
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createElection(@RequestBody ElectionRequest req) {
        if (req == null) {
            return ResponseEntity.badRequest().body("request body required");
        }
        if (req.getTitle() == null || req.getStartsAt() == null || req.getEndsAt() == null) {
            return ResponseEntity.badRequest().body("title, startsAt and endsAt are required");
        }
        if (req.getEndsAt().isBefore(req.getStartsAt())) {
            return ResponseEntity.badRequest().body("endsAt must be after startsAt");
        }

        // Your ElectionService.create signature requires 4 args:
        // create(String title, String someStringField, Instant startsAt, Instant endsAt)
        // We pass an empty string for the second parameter — change this if your DTO contains that field.
        String secondStringPlaceholder = ""; // e.g. description or slug if you add it to ElectionRequest
        Election created = electionService.create(req.getTitle(), secondStringPlaceholder, req.getStartsAt(), req.getEndsAt());

        return ResponseEntity.status(201).body(created);
    }

    // helper: tries method names in order and returns first successful invocation result or null
    private Object invokeIfExists(Object target, String... methodNames) {
        for (String name : methodNames) {
            try {
                Method m = target.getClass().getMethod(name);
                if (m != null) {
                    return m.invoke(target);
                }
            } catch (NoSuchMethodException ignored) {
                // try next
            } catch (Exception ex) {
                // any other exception -> return null (safe fail)
                return null;
            }
        }
        return null;
    }
}
