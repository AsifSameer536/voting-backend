package com.example.voting.dto;

public class CandidateRequest {
    private Long electionId;
    private String name;
    private String manifesto;

    public CandidateRequest() {}

    public CandidateRequest(Long electionId, String name, String manifesto) {
        this.electionId = electionId;
        this.name = name;
        this.manifesto = manifesto;
    }

    public Long getElectionId() {
        return electionId;
    }

    public void setElectionId(Long electionId) {
        this.electionId = electionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getManifesto() {
        return manifesto;
    }

    public void setManifesto(String manifesto) {
        this.manifesto = manifesto;
    }
}
