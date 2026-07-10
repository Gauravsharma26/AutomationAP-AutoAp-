package com.gaurav.autoap.playground;

import dev.langchain4j.service.UserMessage;

public interface ExtractorTest {
    SimplePerson extractPerson(@UserMessage String text);
}
