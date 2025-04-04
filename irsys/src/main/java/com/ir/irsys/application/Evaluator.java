package com.ir.irsys.application;

import java.util.List;
import java.util.Set;

public class Evaluator {

    public static double calculatePrecision(List<String> retrievedDocIds, Set<String> relevantDocIds, int k) {
        int retrievedSize = Math.min(k, retrievedDocIds.size());
        int relevantCount = 0;

        for (int i = 0; i < retrievedSize; i++) {
            if (relevantDocIds.contains(retrievedDocIds.get(i))) {
                relevantCount++;
            }
        }
        return (double) relevantCount / retrievedSize;
    }

    public static double calculateRecall(List<String> retrievedDocIds, Set<String> relevantDocIds, int k) {
        int retrievedSize = Math.min(k, retrievedDocIds.size());
        int relevantCount = 0;

        for (int i = 0; i < retrievedSize; i++) {
            if (relevantDocIds.contains(retrievedDocIds.get(i))) {
                relevantCount++;
            }
        }
        return (double) relevantCount / relevantDocIds.size();
    }
}
