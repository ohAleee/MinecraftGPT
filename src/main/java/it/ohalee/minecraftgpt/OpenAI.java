package it.ohalee.minecraftgpt;

import com.theokanning.openai.completion.CompletionRequest;
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

        return CompletableFuture.supplyAsync(() -> service.createCompletion(CompletionRequest.builder()
                        .prompt(cached.toString())
                        .model(section.getString("model"))
                        .temperature(section.getDouble("temperature"))
                        .maxTokens(section.getInt("max-tokens"))
                        .topP(section.getDouble("top-p"))
                        .frequencyPenalty(section.getDouble("frequency-penalty"))
                        .presencePenalty(section.getDouble("presence-penalty"))
                        .stop(Arrays.asList("Human:", "AI:"))
                        .build())
                .getChoices().get(0).getText()).exceptionally(throwable -> {
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
