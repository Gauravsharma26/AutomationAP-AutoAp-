package com.gaurav.autoap.playground;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaChatModel;

public class TestLLM {

    public static void main(String []args){
        ChatLanguageModel model= OllamaChatModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName("llama3.2:3b")
                .build();
        String response= model.generate("Give greetings of the day");
        System.out.println("Model says :"+response);
    }
}
