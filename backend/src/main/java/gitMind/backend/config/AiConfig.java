package gitMind.backend.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.mistralai.MistralAiEmbeddingModel;
import org.springframework.ai.mistralai.MistralAiEmbeddingOptions;
import org.springframework.ai.mistralai.api.MistralAiApi;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Wires one {@link ChatModel} and one {@link EmbeddingModel}.
 *
 * <p>Chat: Groq (free) or OpenAI — controlled by {@code AI_PROVIDER}.
 * <p>Embeddings: always Mistral AI {@code mistral-embed} (free, 1024 dims).
 * Set {@code PGVECTOR_DIMENSIONS=1024}.
 */
@Configuration
public class AiConfig {

    private static final Logger log = LoggerFactory.getLogger(AiConfig.class);
    private static final String GROQ_BASE_URL = "https://api.groq.com/openai";

    @Value("${ai.provider:groq}")
    private String provider;

    // ── OpenAI ────────────────────────────────────────────────────────────────
    @Value("${spring.ai.openai.api-key:}")
    private String openAiKey;

    @Value("${spring.ai.openai.chat.options.model:gpt-4o-mini}")
    private String openAiChatModel;

    // ── Groq ──────────────────────────────────────────────────────────────────
    @Value("${groq.api-key:}")
    private String groqApiKey;

    @Value("${groq.chat.model:openai/gpt-oss-120b}")
    private String groqChatModel;

    // ── Mistral (embeddings) ──────────────────────────────────────────────────
    @Value("${mistral.api-key:}")
    private String mistralApiKey;

    @Value("${mistral.embedding.model:mistral-embed}")
    private String mistralEmbeddingModel;

    // ── Beans ─────────────────────────────────────────────────────────────────

    @Bean
    ChatModel chatModel() {
        if ("groq".equalsIgnoreCase(provider)) {
            if (groqApiKey.isBlank()) {
                log.warn("AI_PROVIDER=groq but GROQ_API_KEY is not set — chat will fail at request time");
                return new UnconfiguredChatModel();
            }
            log.info("AI: using Groq chat ({})", groqChatModel);
            return OpenAiChatModel.builder()
                    .openAiApi(OpenAiApi.builder()
                            .apiKey(groqApiKey)
                            .baseUrl(GROQ_BASE_URL)
                            .build())
                    .defaultOptions(OpenAiChatOptions.builder().model(groqChatModel).build())
                    .build();
        }

        // fallback: openai
        if (openAiKey.isBlank()) {
            log.warn("AI_PROVIDER=openai but OPENAI_API_KEY is not set — chat will fail at request time");
            return new UnconfiguredChatModel();
        }
        log.info("AI: using OpenAI chat ({})", openAiChatModel);
        return OpenAiChatModel.builder()
                .openAiApi(OpenAiApi.builder().apiKey(openAiKey).build())
                .defaultOptions(OpenAiChatOptions.builder().model(openAiChatModel).build())
                .build();
    }

    @Bean
    EmbeddingModel embeddingModel() {
        if (mistralApiKey.isBlank()) {
            log.warn("MISTRAL_API_KEY is not set — embeddings will fail at request time");
            return new UnconfiguredEmbeddingModel();
        }
        log.info("AI: using Mistral embeddings ({})", mistralEmbeddingModel);
        return new MistralAiEmbeddingModel(
                new MistralAiApi(mistralApiKey),
                MetadataMode.EMBED,
                MistralAiEmbeddingOptions.builder().withModel(mistralEmbeddingModel).build(),
                RetryTemplate.defaultInstance());
    }
}
