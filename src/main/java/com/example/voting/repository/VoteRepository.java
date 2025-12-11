package com.example.voting.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collector;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.voting.model.Candidate;
import com.example.voting.model.Vote;

public interface VoteRepository extends JpaRepository<Vote, Long> {

    boolean existsByVoterIdAndElectionId(Long voterId, Long electionId);

    Optional<Vote> findByVoterIdAndElectionId(Long voterId, Long electionId);

    long countByCandidateId(Long candidateId);

    @Query("SELECT v FROM Vote v WHERE v.election.id = :electionId")
    List<Vote> findByElectionId(@Param("electionId") Long electionId);

    public Collector<Candidate, ?, Map<Long, Object>> countByElectionIdAndCandidateId(Long electionId, Long id);
}
