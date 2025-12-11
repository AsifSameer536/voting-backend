package com.example.voting.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.voting.dto.CandidateRequest;
import com.example.voting.dto.CandidateResponse;
import com.example.voting.model.Candidate;
import com.example.voting.model.Election;
import com.example.voting.repository.CandidateRepository;
import com.example.voting.repository.ElectionRepository;

@RestController
@RequestMapping("/api/candidates")
public class CandidateController {

    private final CandidateRepository candidateRepo;
    private final ElectionRepository electionRepo;

    public CandidateController(CandidateRepository candidateRepo,
                               ElectionRepository electionRepo) {
        this.candidateRepo = candidateRepo;
        this.electionRepo = electionRepo;
    }

    @PostMapping
    public ResponseEntity<?> addCandidate(@RequestBody CandidateRequest req) {

        Election election = electionRepo.findById(req.getElectionId())
                .orElseThrow(() -> new IllegalArgumentException("Election not found"));

        Candidate c = new Candidate();
        c.setName(req.getName());
        c.setManifesto(req.getManifesto());
        c.setElection(election);

        candidateRepo.save(c);

        return ResponseEntity.ok("Candidate added");
    }

    @GetMapping("/election/{id}")
    public ResponseEntity<?> listCandidates(@PathVariable Long id) {

        List<CandidateResponse> list = candidateRepo.findByElectionId(id)
                .stream()
                .map(c -> new CandidateResponse(
                        c.getId(),
                        c.getName(),
                        c.getManifesto(),
                        c.getCreatedAt()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(list);
    }
}
