package com.example.voting.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.voting.model.Candidate;

public interface CandidateRepository extends JpaRepository<Candidate, Long> {

    @Query("SELECT c FROM Candidate c WHERE c.election.id = :electionId")
    List<Candidate> findByElectionId(@Param("electionId") Long electionId);
}
