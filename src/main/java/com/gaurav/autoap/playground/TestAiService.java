package com.gaurav.autoap.playground;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.service.AiServices;

public class TestAiService {
    public static void main(String []args){
        ChatLanguageModel model = OllamaChatModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName("llama3.2:3b")
                .build();
        HelloAgent agent = AiServices.create(HelloAgent.class, model);
        String response = agent.chat("What's a good name for a pet goldfish?");
        System.out.println("Agent response: "+response);
}

}
