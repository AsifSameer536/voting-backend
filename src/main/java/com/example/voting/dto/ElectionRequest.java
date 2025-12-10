package com.example.voting.dto;

import java.time.Instant;

public class ElectionRequest {
    private String title;
    private Instant startsAt;
    private Instant endsAt;

    public ElectionRequest() {}

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Instant getStartsAt() { return startsAt; }
    public void setStartsAt(Instant startsAt) { this.startsAt = startsAt; }

    public Instant getEndsAt() { return endsAt; }
    public void setEndsAt(Instant endsAt) { this.endsAt = endsAt; }
}
