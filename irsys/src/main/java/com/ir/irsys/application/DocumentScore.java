package com.ir.irsys.application;

public class DocumentScore {
    private String documentId;
    private String documentText;
    private double score;

    public DocumentScore(String documentId, String documentText, double score) {
        this.documentId = documentId;
        this.documentText = documentText;
        this.score = score;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getDocumentId() {
        return documentId;
    }

    public String getDocumentText() {
        return documentText;
    }

    public void setDocumentText(String documentText) {
        this.documentText = documentText;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "DocumentScore{" +
                "documentId='" + documentId + '\'' +
                ", score=" + score +
                '}';
    }
}
