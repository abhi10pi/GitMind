package gitMind.backend.config;

import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;

import java.util.List;

public class UnconfiguredEmbeddingModel implements EmbeddingModel {
    @Override
    public EmbeddingResponse call(EmbeddingRequest request) {
        throw new IllegalStateException(
                "No AI provider is configured. Set AI_PROVIDER and the corresponding credentials.");
    }

    @Override
    public float[] embed(Document document) {
        throw new IllegalStateException(
                "No AI provider is configured. Set AI_PROVIDER and the corresponding credentials.");
    }
}
