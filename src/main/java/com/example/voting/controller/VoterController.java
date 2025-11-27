package com.example.voting.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
    public ResponseEntity<?> me(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body("unauthenticated");
        }

        String username = authentication.getName();

        Optional<Voter> opt = voterService.findByUsername(username);
        if (opt.isPresent()) {
            Voter v = opt.get();
            Map<String, Object> body = new HashMap<>();
            body.put("id", v.getId());
            body.put("username", v.getUsername());
            body.put("fullName", v.getFullName());
            body.put("role", v.getRole());
            body.put("createdAt", v.getCreatedAt());
            return ResponseEntity.ok(body);
        } else {
            return ResponseEntity.status(404).body("user not found");
        }
    }
}
