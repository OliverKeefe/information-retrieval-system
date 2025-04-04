package com.ir.irsys.config;

import com.ir.irsys.application.BagOfWords;
import com.ir.irsys.application.Ranker;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class IRConfig {

    @Bean
    public MongoClient mongoClient() {
        return MongoClients.create("mongodb://admin:password@localhost:27017/?authSource=admin");
    }

    @Bean
    public BagOfWords bagOfWords(MongoClient mongoClient) {
        return new BagOfWords(mongoClient, "cranfield_db", "documents");
    }

    @Bean
    public Ranker ranker(BagOfWords bow) {
        List<String> docIds = List.of("200", "230", "247", "250", "289");
        List<String> texts = bow.readDocuments();

        return new Ranker(bow, docIds, texts);
    }
}
