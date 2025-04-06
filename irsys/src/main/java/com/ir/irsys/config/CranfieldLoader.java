package com.ir.irsys.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.io.*;
import java.nio.file.*;
import java.util.UUID;

public class CranfieldLoader {

    private final MongoCollection<Document> collection;

    public CranfieldLoader(MongoClient mongoClient, String dbName, String collectionName) {
        MongoDatabase db = mongoClient.getDatabase(dbName);
        this.collection = db.getCollection(collectionName);
    }

    public void loadCranfieldDocuments(String directoryPath) throws IOException {
        Path dir = Paths.get(directoryPath);

        if (!Files.exists(dir) || !Files.isDirectory(dir)) {
            throw new IllegalArgumentException("Directory does not exist: " + directoryPath);
        }

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*.txt")) {
            for (Path path : stream) {
                String content = Files.readString(path).trim();

                Document doc = new Document()
                        .append("_id", UUID.randomUUID().toString())
                        .append("filename", path.getFileName().toString())
                        .append("text", content);

                collection.insertOne(doc);
                System.out.println("Inserted: " + path.getFileName());
            }
        }
    }
}