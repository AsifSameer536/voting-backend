package com.example.voting.dto;

import java.time.Instant;

public class CandidateResponse {
    private Long id;
    private String name;
    private String manifesto;
    private Instant createdAt;

    public CandidateResponse() {}

    public CandidateResponse(Long id, String name, String manifesto, Instant createdAt) {
        this.id = id;
        this.name = name;
        this.manifesto = manifesto;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getManifesto() { return manifesto; }
    public void setManifesto(String manifesto) { this.manifesto = manifesto; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
