package com.example.voting.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.voting.model.Vote;
import com.example.voting.service.ElectionService;
import com.example.voting.service.VoteService;
import com.example.voting.service.VoterService;

@RestController
@RequestMapping("/votes")
public class VoteController {

    private final VoteService voteService;
    private final VoterService voterService;
    private final ElectionService electionService;

    public VoteController(VoteService voteService,
                          VoterService voterService,
                          ElectionService electionService) {
        this.voteService = voteService;
        this.voterService = voterService;
        this.electionService = electionService;
    }

    /**
     * POST /votes/cast
     * Body: { "electionId": 1, "candidateId": 2 }
     * Auth: Bearer token for voter
     */
    @PostMapping("/cast")
    public ResponseEntity<?> cast(@RequestBody Map<String, Long> body, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body("unauthenticated");
        }

        Long candidateId = body.get("candidateId");
        Long electionId = body.get("electionId");
        if (candidateId == null || electionId == null) {
            return ResponseEntity.badRequest().body("candidateId and electionId required");
        }

        // Resolve voter id from authentication principal
        String username = authentication.getName();
        var optVoter = voterService.findByUsername(username);
        if (optVoter.isEmpty()) {
            return ResponseEntity.status(404).body("voter not found");
        }
        Long voterId = optVoter.get().getId();

        try {
            Vote v = voteService.castVote(voterId, electionId, candidateId);
            return ResponseEntity.status(201).body(Map.of(
                    "voteId", v.getId(),
                    "candidateId", v.getCandidate().getId(),
                    "electionId", v.getElection().getId()
            ));
        } catch (IllegalArgumentException e) {
            // entity not found or mismatch
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (IllegalStateException e) {
            // inactive election or double vote
            // If message mentions "already", return 409; else also 409 for not active
            return ResponseEntity.status(409).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("internal error");
        }
    }

    /**
     * GET /votes/results/{electionId}
     * Authenticated users only.
     * - Admins can view anytime.
     * - Regular voters can view only after election state == "CLOSED".
     */
    @GetMapping("/results/{electionId}")
    public ResponseEntity<?> results(@PathVariable Long electionId, Authentication authentication) {
        var optElection = electionService.findById(electionId);
        if (optElection.isEmpty()) {
            return ResponseEntity.status(404).body("election not found");
        }
        var election = optElection.get();

        boolean isAdmin = false;
        if (authentication != null && authentication.isAuthenticated()) {
            isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        }

        if (!isAdmin && !"CLOSED".equals(election.getState())) {
            return ResponseEntity.status(403).body("results not available until election is CLOSED");
        }

        var results = voteService.getResults(electionId);
        return ResponseEntity.ok(results);
    }
}
