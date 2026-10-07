package gitMind.backend.config;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;

public class UnconfiguredChatModel implements ChatModel {
    @Override
    public ChatResponse call(Prompt prompt) {
        throw new IllegalStateException(
                "No AI provider is configured. Set AI_PROVIDER and the corresponding credentials.");
    }
}
