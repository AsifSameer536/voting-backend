package com.example.voting.service;

import com.example.voting.model.Candidate;
import com.example.voting.model.Election;
import com.example.voting.model.Vote;
import com.example.voting.model.Voter;
import com.example.voting.repository.CandidateRepository;
import com.example.voting.repository.ElectionRepository;
import com.example.voting.repository.VoteRepository;
import com.example.voting.repository.VoterRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.stream.Collectors;

@Service
public class VoteService {

    private final VoteRepository voteRepository;
    private final VoterRepository voterRepository;
    private final CandidateRepository candidateRepository;
    private final ElectionRepository electionRepository;

    public VoteService(VoteRepository voteRepository,
                       VoterRepository voterRepository,
                       CandidateRepository candidateRepository,
                       ElectionRepository electionRepository) {
        this.voteRepository = voteRepository;
        this.voterRepository = voterRepository;
        this.candidateRepository = candidateRepository;
        this.electionRepository = electionRepository;
    }

    @Transactional
    public Vote castVote(Long voterId, Long candidateId, Long electionId) {

        Voter voter = voterRepository.findById(voterId)
                .orElseThrow(() -> new IllegalArgumentException("Voter not found"));

        Election election = electionRepository.findById(electionId)
                .orElseThrow(() -> new IllegalArgumentException("Election not found"));

        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new IllegalArgumentException("Candidate not found"));

        if (!candidate.getElection().getId().equals(electionId)) {
            throw new IllegalArgumentException("Candidate does not belong to election");
        }

        boolean voted = voteRepository
                .findByVoterIdAndElectionId(voterId, electionId)
                .isPresent();

        if (voted) {
            throw new IllegalStateException("You already voted in this election");
        }

        Vote vote = new Vote();
        vote.setElection(election);
        vote.setCandidate(candidate);
        vote.setVoter(voter);

        return voteRepository.save(vote);
    }

    @Transactional(readOnly = true)
    public Map<Long, Long> getResults(Long electionId) {

        return candidateRepository.findByElectionId(electionId)
                .stream()
                .collect(Collectors.toMap(
                        Candidate::getId,
                        c -> voteRepository.countByElectionIdAndCandidateId(electionId, c.getId())
                ));
    }
}
