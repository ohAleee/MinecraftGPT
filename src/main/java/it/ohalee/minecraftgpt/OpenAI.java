package it.ohalee.minecraftgpt;

import com.theokanning.openai.OpenAiService;
import com.theokanning.openai.completion.CompletionRequest;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

public class OpenAI {

    private static final OpenAiService service = new OpenAiService("");

    public static CompletableFuture<String> getResponse(StringBuilder cached, String message) {
        cached.append("\nHuman:").append(message).append("\nAI:");

        return CompletableFuture.supplyAsync(() -> {
            CompletionRequest request = CompletionRequest.builder()
                    .prompt(cached.toString())
                    .model("text-davinci-003")
                    .temperature(0.9)
                    .maxTokens(150)
                    .topP(1.0)
                    .frequencyPenalty(0.0)
                    .presencePenalty(0.6)
                    .stop(Arrays.asList("Human:", "AI:"))
                    .build();
            return service.createCompletion(request).getChoices().get(0).getText();
        });
    }

}
