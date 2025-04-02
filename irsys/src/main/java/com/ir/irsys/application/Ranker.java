package com.ir.irsys.application;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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





}
