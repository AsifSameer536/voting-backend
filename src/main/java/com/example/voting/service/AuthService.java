package com.example.voting.service;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.voting.dto.AuthResponse;
import com.example.voting.dto.LoginRequest;
import com.example.voting.dto.RegisterRequest;
import com.example.voting.model.Voter;
import com.example.voting.repository.VoterRepository;
import com.example.voting.security.JwtUtil;

@Service
public class AuthService {

    private final VoterRepository voterRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthService(VoterRepository voterRepository,
                       BCryptPasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtUtil jwtUtil) {
        this.voterRepository = voterRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    public void register(RegisterRequest req) {
        if (voterRepository.existsByUsername(req.username)) {
            throw new IllegalArgumentException("Username already exists");
        }
        Voter v = new Voter();
        v.setUsername(req.username);
        v.setPassword(passwordEncoder.encode(req.password));
        v.setFullName(req.fullName);
        v.setRole("ROLE_VOTER");
        try {
            voterRepository.save(v);
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException("Username conflict");
        }
    }

    public AuthResponse login(LoginRequest req) {
        // authenticate
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.username, req.password)
        );

        Voter v = voterRepository.findByUsername(req.username)
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        String token = jwtUtil.generateToken(v.getUsername(), v.getRole());
        return new AuthResponse(token);
    }
}
