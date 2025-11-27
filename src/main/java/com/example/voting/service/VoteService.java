package com.example.voting.service;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.voting.model.Candidate;
import com.example.voting.model.Election;
import com.example.voting.model.Vote;
import com.example.voting.model.Voter;
import com.example.voting.repository.CandidateRepository;
import com.example.voting.repository.ElectionRepository;
import com.example.voting.repository.VoteRepository;
import com.example.voting.repository.VoterRepository;

@Service
public class VoteService {

    private final VoteRepository voteRepository;
    private final VoterRepository voterRepository;
    private final CandidateRepository candidateRepository;
    private final ElectionRepository electionRepository;
    private final ElectionService electionService;

    public VoteService(VoteRepository voteRepository,
                       VoterRepository voterRepository,
                       CandidateRepository candidateRepository,
                       ElectionRepository electionRepository,
                       ElectionService electionService) {
        this.voteRepository = voteRepository;
        this.voterRepository = voterRepository;
        this.candidateRepository = candidateRepository;
        this.electionRepository = electionRepository;
        this.electionService = electionService;
    }

    /**
     * Cast a vote. Enforces single-vote per voter per election.
     * Enforces election must be active (uses ElectionService.isActive()).
     *
     * Throws:
     *  - IllegalArgumentException for missing entities or mismatch
     *  - IllegalStateException for double vote or inactive election
     */
    @Transactional
    public Vote castVote(Long voterId, Long electionId, Long candidateId) {
        // validate existence
        Voter voter = voterRepository.findById(voterId)
                .orElseThrow(() -> new IllegalArgumentException("Voter not found: " + voterId));
        Election election = electionRepository.findById(electionId)
                .orElseThrow(() -> new IllegalArgumentException("Election not found: " + electionId));
        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new IllegalArgumentException("Candidate not found: " + candidateId));

        // candidate must belong to election
        if (candidate.getElection() == null || !candidate.getElection().getId().equals(election.getId())) {
            throw new IllegalArgumentException("Candidate does not belong to election");
        }

        // ENFORCE: election must be active (uses ElectionService, which checks state and optional schedule)
        if (!electionService.isActive(election)) {
            throw new IllegalStateException("Election is not active");
        }

        // Single-vote enforcement (application-level); DB unique constraint is a second line of defense
        boolean already = voteRepository.findByVoterIdAndElectionId(voterId, electionId).isPresent();
        if (already) {
            throw new IllegalStateException("Voter already cast a vote in this election");
        }

        Vote vote = new Vote();
        vote.setVoter(voter);
        vote.setElection(election);
        vote.setCandidate(candidate);
        return voteRepository.save(vote);
    }

    /**
     * Results: return map candidateId -> voteCount for an election.
     * Simple and correct for small datasets. For large datasets consider a JPQL COUNT GROUP BY for performance.
     */
    @Transactional(readOnly = true)
    public Map<Long, Long> getResults(Long electionId) {
        return candidateRepository.findAll().stream()
                .filter(c -> c.getElection() != null && c.getElection().getId().equals(electionId))
                .collect(Collectors.toMap(
                        Candidate::getId,
                        c -> voteRepository.countByElectionIdAndCandidateId(electionId, c.getId())
                ));
    }
}
