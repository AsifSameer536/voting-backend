package com.example.voting.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.voting.model.Voter;
import com.example.voting.service.VoterService;

@RestController
@RequestMapping("/voters")
public class VoterController {

    private final VoterService voterService;

    public VoterController(VoterService voterService) {
        this.voterService = voterService;
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication auth) {

        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }

        String username = auth.getName();

        Voter voter = voterService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Voter not found"));

        Map<String, Object> body = new HashMap<>();
        body.put("id", voter.getId());
        body.put("username", voter.getUsername());
        body.put("fullName", voter.getFullName());
        body.put("role", voter.getRole());
        body.put("createdAt", voter.getCreatedAt());

        return ResponseEntity.ok(body);
    }
}
