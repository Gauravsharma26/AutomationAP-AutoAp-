package com.gaurav.autoap.playground;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.service.AiServices;

public class TestExtraction {
    public static void main(String []args){
        ChatLanguageModel model= OllamaChatModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName("llama3.2:3b")
                .build();
        ExtractorTest extractor= AiServices.create(ExtractorTest.class,model);
        SimplePerson result=extractor.extractPerson(
                "John is a 34 year old software engineer living in Berlin."
        );
        System.out.println(result);
    }
}
