package com.example.voting.dto;

import java.time.Instant;

public class UpdateElectionRequest {
    private String title;
    private String description;
    private Instant startAt;
    private Instant endAt;
    private String state; // optional: ADMIN can set ACTIVE/CLOSED/UPCOMING

    // getters/setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Instant getStartAt() { return startAt; }
    public void setStartAt(Instant startAt) { this.startAt = startAt; }
    public Instant getEndAt() { return endAt; }
    public void setEndAt(Instant endAt) { this.endAt = endAt; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
}
