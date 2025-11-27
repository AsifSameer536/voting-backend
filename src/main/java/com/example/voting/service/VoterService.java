package com.example.voting.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.voting.model.Voter;
import com.example.voting.repository.VoterRepository;

@Service
public class VoterService {

    private final VoterRepository voterRepository;

    public VoterService(VoterRepository voterRepository) {
        this.voterRepository = voterRepository;
    }

    public Optional<Voter> findByUsername(String username) {
        return voterRepository.findByUsername(username);
    }
}
