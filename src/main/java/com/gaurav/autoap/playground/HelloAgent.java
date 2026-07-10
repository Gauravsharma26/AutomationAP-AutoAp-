package com.gaurav.autoap.playground;

import dev.langchain4j.service.SystemMessage;

public interface HelloAgent {
    @SystemMessage("You are a friendly assistant. Respond in one short sentence")
    String chat(String userMessage);
}
