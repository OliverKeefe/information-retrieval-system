package com.ir.irsys.application;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.*;
import java.util.stream.Collectors;


/**
 * <h2>Bag-Of-Words Model.</h2>
 *
 * <h3>BoW model for the Cranfield collection.</h3>
 *
 * <p> Model does the following:</p>
 *
 *   <li>
 *      <ul>Reads the document collection,</ul>
 *      <ul>Preprocesses the text (e.g. tokenization, stop-word removal),</ul>
 *      <ul>Build a vocabulary and represents each document as a vector of word counts.</ul>
 *   </li>
 *
 * <p> Obviously you'd usually do this sort of thing in MATLAB or Python3 but because I'm going to
 * be implementing an IR System in Java for HeelStrike, I thought I'd bite the bullet and just do
 * it in Java 22.</p>
 *
 * */
public class BagOfWords {

    private MongoCollection<Document> documentCollection;
    private Set<String> stopWords;
    private Map<String, Integer> vocabulary;


    /**
     * Constructor initializes BagOfWords instance.
     *
     * @param mongoClient - The MongoClient to connect to MongoDB.
     * @param dbName - The name of the database (duh).
     * @param collectionName - The name of the collection that stores the documents.
     * */
    public BagOfWords(MongoClient mongoClient, String dbName, String collectionName) {
        MongoDatabase mongodb = mongoClient.getDatabase(dbName);
        this.documentCollection = mongodb.getCollection(collectionName);
        this.stopWords = loadStopWords();
        this.vocabulary = new HashMap<>();
    }


    /**
     * <h2>Read Documents</h2>
     *
     * @return - List of document texts.
     * */
    public List<String> readDocuments() {
        List<String> documents = new ArrayList<>();

        for (Document document : documentCollection.find()) {
            String text = document.getString("text");
            documents.add(text);
        }

        return documents;
    }

    /**
     * <h2>Pre-process Text</h2>
     *
     * <p>Removes stop words, non-text characters etc...</p>
     *
     * @param text - The raw text of the document.
     * @return - A list of tokens after preprocessing.
     * */
    public List<String> preprocessText(String text) {
        String[] tokens = text.toLowerCase().split("\\W+");

        return Arrays.stream(tokens)
                .filter(token -> !stopWords.contains(token) && !token.isEmpty())
                .collect(Collectors.toList());
    }

    /**
     * <h2>Build Vocabulary</h2>
     *
     * @param tokenizedDocuments - A list where each element is a list of tokens from a document.
     * */
    public void buildVocabulary(List<List<String>> tokenizedDocuments) {
        for (List<String> tokens : tokenizedDocuments) {
            for (String token : tokens) {
                vocabulary.put(token, vocabulary.getOrDefault(token, 0) + 1);
            }
        }
    }

    /**
     * <h2>Vectorize Document</h2>
     *
     * @param tokens - A list of tokens from the document.
     * @return - A map representing the document vector, where keys are words and values are their counts.
     * */
    public Map<String, Integer> vectorizeDocument(List<String> tokens) {
        Map<String, Integer> vector = new HashMap<>();

        for (String token : tokens) {
            vector.put(token, vector.getOrDefault(token, 0) + 1);
        }

        return vector;
    }

    /**
     * <h2>Load Stop Words</h2>
     * <p>Loads a set of stop words.</p>
     *
     * </br>
     *
     * <p>
     *     Stop words are things like joining words (and, to, of) etc...
     *     So words that don't provide any context as to the substance of the document.
     * </p>
     *
     * @return - A set of stop words.
     */
    private Set<String> loadStopWords() {
        return new HashSet<>(Arrays.asList("a", "an", "the", "and", "or", "but", "if", "in", "on", "with", "to", "of"));
    }

    /**
     * Gets the built vocabulary.
     *
     * @return - Vocabulary as a map where keys are words and values are their frequencies.
     */
    public Map<String, Integer> getVocabulary() {
        return vocabulary;
    }
}
