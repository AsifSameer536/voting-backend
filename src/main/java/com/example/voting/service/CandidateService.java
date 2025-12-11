package com.example.voting.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.voting.model.Candidate;
import com.example.voting.model.Election;
import com.example.voting.repository.CandidateRepository;
import com.example.voting.repository.ElectionRepository;

@Service
public class CandidateService {

    private final CandidateRepository candidateRepository;
    private final ElectionRepository electionRepository;

    public CandidateService(CandidateRepository candidateRepository,
                            ElectionRepository electionRepository) {
        this.candidateRepository = candidateRepository;
        this.electionRepository = electionRepository;
    }

    public List<Candidate> findAll() {
        return candidateRepository.findAll();
    }

    public Optional<Candidate> findById(Long id) {
        return candidateRepository.findById(id);
    }

    @Transactional
    public Candidate create(Long electionId, String name, String manifesto) {
        Election election = electionRepository.findById(electionId)
                .orElseThrow(() -> new IllegalArgumentException("Election not found: " + electionId));

        Candidate c = new Candidate();
        c.setElection(election);
        c.setName(name);
        c.setManifesto(manifesto);

        return candidateRepository.save(c);
    }

    @Transactional
    public Candidate update(Long id, Long electionId, String name, String manifesto) {
        Candidate c = candidateRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Candidate not found: " + id));

        if (electionId != null) {
            Election election = electionRepository.findById(electionId)
                    .orElseThrow(() -> new IllegalArgumentException("Election not found: " + electionId));
            c.setElection(election);
        }

        if (name != null) c.setName(name);
        if (manifesto != null) c.setManifesto(manifesto);

        return candidateRepository.save(c);
    }

    @Transactional
    public void delete(Long id) {
        candidateRepository.deleteById(id);
    }
}
