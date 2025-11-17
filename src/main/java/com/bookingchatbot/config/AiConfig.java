package com.bookingchatbot.config;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.TokenWindowChatMemory;
import dev.langchain4j.model.Tokenizer;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.nio.charset.StandardCharsets;

/**
 * Configuration for AI/LangChain4j components.
 * Sets up embedding store, chat memory, and RAG (Retrieval Augmented Generation).
 */
@Configuration
public class AiConfig {

    private static final Logger logger = LoggerFactory.getLogger(AiConfig.class);

    @Bean
    ChatMemoryProvider chatMemoryProvider(Tokenizer tokenizer) {
        return chatId -> TokenWindowChatMemory.withMaxTokens(1000, tokenizer);
    }

    @Bean
    EmbeddingStore<TextSegment> embeddingStore() {
        return new InMemoryEmbeddingStore<>();
    }

    @Bean
    ContentRetriever contentRetriever(
            EmbeddingStore<TextSegment> embeddingStore,
            EmbeddingModel embeddingModel) {
        return EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(2)
                .minScore(0.6)
                .build();
    }

    /**
     * Ingests the terms of service document into the embedding store.
     * This enables the AI to answer questions about policies using RAG.
     */
    @Bean
    CommandLineRunner ingestDocuments(
            EmbeddingModel embeddingModel,
            EmbeddingStore<TextSegment> embeddingStore,
            Tokenizer tokenizer,
            @Value("classpath:terms-of-service.txt") Resource termsOfService) {
        return args -> {
            try (var inputStream = termsOfService.getInputStream()) {
                String text = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                Metadata metadata = new Metadata()
                        .put(Document.FILE_NAME, termsOfService.getFilename());
                var doc = new Document(text, metadata);
                var ingestor = EmbeddingStoreIngestor.builder()
                        .documentSplitter(DocumentSplitters.recursive(50, 0, tokenizer))
                        .embeddingModel(embeddingModel)
                        .embeddingStore(embeddingStore)
                        .build();
                ingestor.ingest(doc);
                logger.info("Successfully ingested terms of service document");
            } catch (Exception e) {
                logger.error("Failed to ingest terms of service", e);
                throw new IllegalStateException("Terms of service ingestion failed", e);
            }
        };
    }
}

