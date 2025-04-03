package com.ir.irsys.application;

import javax.print.Doc;
import java.lang.annotation.Documented;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Ranker {

    private BagOfWords bow;
    private List<Map<String, Integer>> documentVectors;
    private List<String> documentIds;
    private List<String> documentTexts;

    public Ranker(BagOfWords bow, List<String> documentIds, List<String> documentTexts) {
        this.bow = bow;
        this.documentIds = documentIds;
        this.documentTexts = documentTexts;
        this.documentVectors = new ArrayList<>();

        for (String text : documentTexts) {
            List<String> tokens = bow.preprocessText(text);
            Map<String, Integer> vector = bow.vectorizeDocument(tokens);
            documentVectors.add(vector);
        }
    }

    /**
     * <h2>Calculate Cosine Similarity</h2>
     *
     * <p>Calculates the cosine similarity between two document vectors.</p>
     *
     * @param vec1 The first document vector.
     * @param vec2 The second document vector.
     * @return Cosine Similarity score.
     * */
    public double cosineSimilarity(Map<String, Integer> vec1, Map<String, Integer> vec2) {
        double dotProduct = 0.0;
        double euclidianNormVec1 = 0.0;
        double euclidianNormVec2 = 0.0;

        for (Map.Entry<String, Integer> entry : vec1.entrySet()) {
            int count1 = entry.getValue();
            euclidianNormVec1 += count1 * count1;
            int count2 = vec2.getOrDefault(entry.getKey(), 0);
            dotProduct += count1 * count2;
        }

        for (int count : vec2.values()) {
            euclidianNormVec2 += count * count;
        }

        double denominator = Math.sqrt(euclidianNormVec1) * Math.sqrt(euclidianNormVec2) + 1e-10;
        return dotProduct / denominator;
    }

    public List<DocumentScore> rankDocumentsForQuery(String query) {
        List<String> queryTokens = bow.preprocessText(query);

        Map<String, Integer> queryVector = bow.vectorizeDocument(queryTokens);

        List<DocumentScore> scores = new ArrayList<>();

        for (int i = 0; i < documentVectors.size(); i++) {
            double similarity = cosineSimilarity(queryVector, documentVectors.get(i));
            scores.add(new DocumentScore(documentIds.get(i), documentTexts.get(i), similarity));
        }

        return scores.stream()
                .sorted(Comparator.comparingDouble(DocumentScore::getScore).reversed())
                .collect(Collectors.toList()); // Potentially unmodifiable list, haven't decided yet.
    }
}
