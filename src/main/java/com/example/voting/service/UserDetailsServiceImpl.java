package com.example.voting.service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.voting.model.Voter;
import com.example.voting.repository.VoterRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final VoterRepository voterRepository;

    public UserDetailsServiceImpl(VoterRepository voterRepository) {
        this.voterRepository = voterRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Voter v = voterRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        // Build Spring Security User object with role from Voter.role
        return User.builder()
                .username(v.getUsername())
                .password(v.getPassword()) // hashed
                .roles(v.getRole().replace("ROLE_", "")) // Spring will prefix ROLE_
                .build();
    }
}
