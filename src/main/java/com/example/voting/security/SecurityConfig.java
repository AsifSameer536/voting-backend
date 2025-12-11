package com.example.voting.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    // ... your Jwt filter beans etc.

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // make GET listing public
                .requestMatchers(HttpMethod.GET, "/api/candidates/**").permitAll()
                // keep other endpoints protected (example)
                .requestMatchers("/api/candidates/**").hasRole("ADMIN")
                .requestMatchers("/api/elections/**").authenticated()
                .anyRequest().authenticated()
            )
            // add your JWT filter registration here (if not already)
            // .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
            ;
        return http.build();
    }
}
