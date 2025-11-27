package com.example.voting.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.voting.model.Candidate;

public interface CandidateRepository extends JpaRepository<Candidate, Long> {
}
