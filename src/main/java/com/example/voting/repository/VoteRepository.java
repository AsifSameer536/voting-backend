package com.example.voting.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.voting.model.Vote;

public interface VoteRepository extends JpaRepository<Vote, Long> {
    Optional<Vote> findByVoterIdAndElectionId(Long voterId, Long electionId);
    long countByElectionIdAndCandidateId(Long electionId, Long candidateId);
    long countByElectionId(Long electionId);
}
