package com.gaurav.autoap.config;

import com.gaurav.autoap.agent.CommunicationAgent;
import com.gaurav.autoap.agent.ExplanationAgent;
import com.gaurav.autoap.agent.ExtractionAgent;
import com.gaurav.autoap.agent.ValidationAgent;
import com.gaurav.autoap.tool.InvoiceTools;
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
    @Bean
    public ValidationAgent validationAgent(ChatLanguageModel chatLanguageModel, InvoiceTools invoiceTools) {
        return AiServices.builder(ValidationAgent.class)
                .chatLanguageModel(chatLanguageModel)
                .tools(invoiceTools)
                .build();
    }
    @Bean
    public ExplanationAgent explanationAgent(ChatLanguageModel chatLanguageModel) {
        return AiServices.create(ExplanationAgent.class, chatLanguageModel);
    }
    @Bean
    public CommunicationAgent communicationAgent(ChatLanguageModel chatLanguageModel) {
        return AiServices.create(CommunicationAgent.class, chatLanguageModel);
    }
}
