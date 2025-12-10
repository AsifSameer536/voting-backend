package com.example.voting.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.voting.model.Vote;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {

    Optional<Vote> findByVoterIdAndElectionId(Long voterId, Long electionId);

    Long countByElectionIdAndCandidateId(Long electionId, Long candidateId);

    boolean existsByVoterIdAndElectionId(Long voterId, Long electionId);
}
