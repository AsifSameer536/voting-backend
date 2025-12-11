package com.example.voting.security;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.voting.service.UserDetailsServiceImpl;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * JWT filter: validates token, loads UserDetails, and sets Authentication.
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserDetailsServiceImpl userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader("Authorization");
        String token = null;

        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            token = header.substring(7);
        }

        if (!StringUtils.hasText(token)) {
            // no token — continue as anonymous; debug log for troubleshooting
            log.debug("No JWT token found in request header for [{} {}]", request.getMethod(), request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }

        try {
            if (!jwtUtil.validateToken(token)) {
                log.warn("JWT token validation failed for request to {} {}", request.getMethod(), request.getRequestURI());
                SecurityContextHolder.clearContext();
                filterChain.doFilter(request, response);
                return;
            }

            String username = jwtUtil.getUsernameFromToken(token);
            if (username == null) {
                log.warn("JWT token did not contain a username");
                SecurityContextHolder.clearContext();
                filterChain.doFilter(request, response);
                return;
            }

            // Load full user details (ensures authorities & account flags are respected)
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (userDetails == null) {
                log.warn("UserDetailsService returned null for username: {}", username);
                SecurityContextHolder.clearContext();
                filterChain.doFilter(request, response);
                return;
            }

            // If JWT stores a role claim and you need to convert it, consider using userDetails authorities.
            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(auth);
            log.debug("Authenticated user '{}' via JWT for request {} {}", username, request.getMethod(), request.getRequestURI());

        } catch (Exception ex) {
            // Log the exception so we know why token parsing/validation failed
            log.warn("Exception while processing JWT authentication: {}", ex.getMessage(), ex);
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}
