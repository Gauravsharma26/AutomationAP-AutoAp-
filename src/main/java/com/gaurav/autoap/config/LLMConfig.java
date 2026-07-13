package com.gaurav.autoap.config;

import com.gaurav.autoap.agent.ExtractionAgent;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.service.AiServices;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LLMConfig {
    @Bean
    public ChatLanguageModel chatLanguageModel() {
        return OllamaChatModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName("llama3.2:3b")
                .build();
    }
    @Bean
    public ExtractionAgent extractionAgent(ChatLanguageModel chatLanguageModel) {
        return AiServices.create(ExtractionAgent.class, chatLanguageModel);
    }
}
