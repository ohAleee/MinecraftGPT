package it.ohalee.minecraftgpt;

import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.service.OpenAiService;
import org.bukkit.configuration.ConfigurationSection;
import retrofit2.HttpException;

import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

public class OpenAI {

    private static OpenAiService service;

    public static CompletableFuture<Void> init(String key) {
        return CompletableFuture.runAsync(() -> service = new OpenAiService(key, Duration.ofSeconds(5)));
    }

    public static CompletableFuture<String> getResponse(ConfigurationSection section, StringBuilder cached, String message) {
        cached.append("\nHuman:").append(message).append("\nAI:");

        return CompletableFuture.supplyAsync(() -> {
            String model = section.getString("model", "text-davinci-003");
            int maxTokens = section.getInt("max-tokens");
            double frequencyPenalty = section.getDouble("frequency-penalty");
            double presencePenalty = section.getDouble("presence-penalty");
            double topP = section.getDouble("top-p");
            double temperature = section.getDouble("temperature");

            return service.createChatCompletion(ChatCompletionRequest.builder()
                            .model(model)
                            .temperature(temperature)
                            .maxTokens(maxTokens)
                            .topP(topP)
                            .frequencyPenalty(frequencyPenalty)
                            .presencePenalty(presencePenalty)
                            .stop(Arrays.asList("Human:", "AI:"))
                            .build())
                    .getChoices().get(0).getMessage().getContent();
        }).exceptionally(throwable -> {
            if (throwable.getCause() instanceof HttpException e) {
                String reason = switch (e.response().code()) {
                    case 401 -> "Invalid API key! Please check your configuration.";
                    case 429 -> "Too many requests! Please wait a few seconds and try again.";
                    case 500 -> "OpenAI service is currently unavailable. Please try again later.";
                    default -> "Unknown error! Please try again later. If this error persists, contact the plugin developer.";
                };
                throw new RuntimeException(reason, throwable);
            }
            throw new RuntimeException(throwable);
        });
    }

}
