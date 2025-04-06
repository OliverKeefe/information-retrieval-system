package com.ir.irsys.application;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.ApplicationScope;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class Ranker {

    private BagOfWords bow;
    private List<Map<String, Integer>> documentVectors;
    private List<String> documentIds;
    private List<String> documentTexts;
    private Map<String, Double> idfScores;
    private List<Map<String, Double>> tfidfDocumentVectors;

    public Ranker(BagOfWords bow, List<String> documentIds, List<String> documentTexts) {
        this.bow = bow;
        this.documentIds = documentIds;
        this.documentTexts = documentTexts;
        this.documentVectors = new ArrayList<>();
        this.tfidfDocumentVectors = new ArrayList<>();

        for (String text : documentTexts) {
            List<String> tokens = bow.preprocessText(text);
            Map<String, Integer> vector = bow.vectorizeDocument(tokens);
            documentVectors.add(vector);
        }

        computeIDFScores();

        for (Map<String, Integer> docVector : documentVectors) {
            tfidfDocumentVectors.add(computeTFIDFForDocument(docVector));
        }
    }

    private void computeIDFScores() {
        idfScores = new HashMap<>();
        int totalDocuments = documentVectors.size();

        Map<String, Integer> documentFrequency = new HashMap<>();
        for (Map<String, Integer> docVector : documentVectors) {
            for (String term : docVector.keySet()) {
                documentFrequency.put(term, documentFrequency.getOrDefault(term, 0) + 1);
            }
        }

        for (Map.Entry<String, Integer> entry : documentFrequency.entrySet()) {
            String term = entry.getKey();
            int df = entry.getValue();
            double idf = Math.log((double) totalDocuments / (double) df);
            idfScores.put(term, idf);
        }
    }

    private Map<String, Double> computeTFIDFForDocument(Map<String, Integer> docVector) {
        Map<String, Double> tfidfVector = new HashMap<>();

        for (Map.Entry<String, Integer> entry : docVector.entrySet()) {
            String term = entry.getKey();
            int tf = entry.getValue();
            double idf = idfScores.getOrDefault(term, 0.0);
            tfidfVector.put(term, tf * idf);
        }

        return tfidfVector;
    }

    /**
     * <h2>Calculate Cosine Similarity for Integer Vectors</h2>
     *
     * <p>Calculates the cosine similarity between two document vectors represented as integer counts.</p>
     *
     * @param vec1 The first document vector.
     * @param vec2 The second document vector.
     * @return Cosine Similarity score.
     */
    public double cosineSimilarityInt(Map<String, Integer> vec1, Map<String, Integer> vec2) {
        double dotProduct = 0.0;
        double euclideanNormVec1 = 0.0;
        double euclideanNormVec2 = 0.0;

        for (Map.Entry<String, Integer> entry : vec1.entrySet()) {
            int count1 = entry.getValue();
            euclideanNormVec1 += count1 * count1;
            int count2 = vec2.getOrDefault(entry.getKey(), 0);
            dotProduct += count1 * count2;
        }

        for (int count : vec2.values()) {
            euclideanNormVec2 += count * count;
        }

        double denominator = Math.sqrt(euclideanNormVec1) * Math.sqrt(euclideanNormVec2) + 1e-10;
        return dotProduct / denominator;
    }

    /**
     * <h2>Calculate Cosine Similarity for TF-IDF Vectors</h2>
     *
     * <p>Calculates the cosine similarity between two document vectors represented as TF-IDF weighted doubles.</p>
     *
     * @param vec1 The first TF-IDF vector.
     * @param vec2 The second TF-IDF vector.
     * @return Cosine Similarity score.
     */
    public double cosineSimilarityDouble(Map<String, Double> vec1, Map<String, Double> vec2) {
        double dotProduct = 0.0;
        double normVec1 = 0.0;
        double normVec2 = 0.0;

        for (Map.Entry<String, Double> entry : vec1.entrySet()) {
            double value1 = entry.getValue();
            normVec1 += value1 * value1;
            double value2 = vec2.getOrDefault(entry.getKey(), 0.0);
            dotProduct += value1 * value2;
        }

        for (double value : vec2.values()) {
            normVec2 += value * value;
        }

        double denominator = Math.sqrt(normVec1) * Math.sqrt(normVec2) + 1e-10;
        return dotProduct / denominator;
    }

    public List<DocumentScore> rankDocumentsForQuery(String query) {
        // Preprocess the query similarly to the documents
        List<String> queryTokens = bow.preprocessText(query);
        // Create a term frequency vector for the query
        Map<String, Integer> queryTFVector = bow.vectorizeDocument(queryTokens);
        // Convert query vector to TF-IDF weighted vector using the computed IDF scores
        Map<String, Double> queryTFIDFVector = new HashMap<>();

        for (Map.Entry<String, Integer> entry : queryTFVector.entrySet()) {
            String term = entry.getKey();
            int tf = entry.getValue();
            double idf = idfScores.getOrDefault(term, 0.0);
            queryTFIDFVector.put(term, tf * idf);
        }

        List<DocumentScore> scores = new ArrayList<>();

        // Compute cosine similarity for each document's TF-IDF vector
        for (int i = 0; i < tfidfDocumentVectors.size(); i++) {
            double similarity = cosineSimilarityDouble(queryTFIDFVector, tfidfDocumentVectors.get(i));
            scores.add(new DocumentScore(documentIds.get(i), documentTexts.get(i), similarity));
        }

        // Sort documents in descending order based on similarity score
        return scores.stream()
                .sorted(Comparator.comparingDouble(DocumentScore::getScore).reversed())
                .collect(Collectors.toList());
    }
}
