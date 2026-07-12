package com.gaurav.autoap.playground;


import com.gaurav.autoap.agent.ExtractionAgent;
import com.gaurav.autoap.model.InvoiceData;
import com.gaurav.autoap.utility.DateParsingUtil;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.service.AiServices;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

public class ExtractionAgentTest {

    public static void main(String[] args) throws Exception {
        ChatLanguageModel model = OllamaChatModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName("llama3.2:3b")
                .build();

        ExtractionAgent agent = AiServices.create(ExtractionAgent.class, model);

        List<String> files = List.of(
                "invoice1.txt", "invoice2.txt", "invoice3.txt", "invoice4.txt", "invoice5.txt"
        );

        for (String file : files) {
            String text = Files.readString(
                    Path.of("src/main/resources/sample-invoices/" + file)
            );

            System.out.println("=== " + file + " ===");
            try {
                InvoiceData result = agent.extract(text);
                System.out.println(result);
                LocalDate parsedInvoiceDate = DateParsingUtil.parseFlexible(result.invoiceDate());
                LocalDate parsedDueDate = DateParsingUtil.parseFlexible(result.dueDate());

                System.out.println("Parsed invoiceDate: " + parsedInvoiceDate);
                System.out.println("Parsed dueDate: " + parsedDueDate);
            } catch (Exception e) {
                System.out.println("FAILED: " + e.getMessage());
            }
            System.out.println();
        }
    }
}