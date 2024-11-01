package com.ai.chess.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class MongoConfig extends AbstractMongoClientConfiguration {
    @Value("${mongodb.database.name}")
    private String databaseName;

    @Value("${mongodb.database.uri}")
    private String connectionURI;

    @NotNull
    @Bean
    public MongoClient mongoClient() {
        ConnectionString connectionString = new ConnectionString(connectionURI);
        MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .build();

        return MongoClients.create(mongoClientSettings);
    }

    @Bean
    public MongoTemplate mongoTemplate() {
        return new MongoTemplate(mongoClient(), databaseName);
    }

    @NotNull
    @Override
    protected String getDatabaseName() {
        return databaseName;
    }
    @Override
    public boolean autoIndexCreation() {
        return true;
    }
}
