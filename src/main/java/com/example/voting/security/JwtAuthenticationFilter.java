package com.example.voting.security;

import com.example.voting.service.UserDetailsServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserDetailsServiceImpl userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader("Authorization");
        log.debug("Incoming Authorization header: {}", header);

        String token = null;
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            token = header.substring(7).trim();
        }

        if (!StringUtils.hasText(token)) {
            log.debug("No Bearer token found in request");
            filterChain.doFilter(request, response);
            return;
        }

        log.debug("Extracted JWT token (first 40 chars): {}", token.length() > 40 ? token.substring(0,40) + "..." : token);

        try {
            boolean valid = jwtUtil.validateToken(token);
            log.debug("jwtUtil.validateToken -> {}", valid);

            if (valid) {
                String username = jwtUtil.getUsernameFromToken(token);
                if (username != null) {
                    log.debug("Token subject (username): {}", username);

                    // Load full UserDetails (principal + authorities)
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(auth);
                    log.debug("SecurityContext set with user: {}", userDetails.getUsername());
                } else {
                    log.warn("Token valid but no subject found");
                }
            } else {
                log.warn("Invalid JWT token");
            }
        } catch (Exception ex) {
            log.error("Exception while validating JWT: {}", ex.getMessage());
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}
