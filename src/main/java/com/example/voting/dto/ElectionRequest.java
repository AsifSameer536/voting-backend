package com.example.voting.dto;

import java.time.Instant;

/**
 * Request DTO for creating/updating elections.
 */
public class ElectionRequest {
    private String title;
    private String description;
    private String state;      // optional: UPCOMING, ACTIVE, FINISHED, ...
    private Instant startsAt;
    private Instant endsAt;

    public ElectionRequest() {}

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public Instant getStartsAt() { return startsAt; }
    public void setStartsAt(Instant startsAt) { this.startsAt = startsAt; }

    public Instant getEndsAt() { return endsAt; }
    public void setEndsAt(Instant endsAt) { this.endsAt = endsAt; }
}
