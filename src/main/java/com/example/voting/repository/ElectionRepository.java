package com.example.voting.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.voting.model.Election;

public interface ElectionRepository extends JpaRepository<Election, Long> {
}
