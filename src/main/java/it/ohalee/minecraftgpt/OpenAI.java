package it.ohalee.minecraftgpt;

import com.theokanning.openai.OpenAiService;
import com.theokanning.openai.completion.CompletionRequest;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

public class OpenAI {

    private static OpenAiService service;

    public static void init(String key) {
        service = new OpenAiService(key, 0);
    }

    public static CompletableFuture<String> getResponse(ConfigurationSection section, StringBuilder cached, String message) {
        cached.append("\nHuman:").append(message).append("\nAI:");

        return CompletableFuture.supplyAsync(() -> {
            CompletionRequest request = CompletionRequest.builder()
                    .prompt(cached.toString())
                    .model(section.getString("model"))
                    .temperature(section.getDouble("temperature"))
                    .maxTokens(section.getInt("max-tokens"))
                    .topP(section.getDouble("top-p"))
                    .frequencyPenalty(section.getDouble("frequency-penalty"))
                    .presencePenalty(section.getDouble("presence-penalty"))
                    .stop(Arrays.asList("Human:", "AI:"))
                    .build();
            return service.createCompletion(request).getChoices().get(0).getText();
        });
    }

}
