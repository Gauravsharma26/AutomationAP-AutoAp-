package com.gaurav.autoap.playground;



import com.gaurav.autoap.AutoapApplication;
import com.gaurav.autoap.agent.ValidationAgent;
import com.gaurav.autoap.model.InvoiceData;
import com.gaurav.autoap.model.LineItem;
import com.gaurav.autoap.model.ValidationResult;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.math.BigDecimal;
import java.util.List;

public class ValidationAgentTest {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(AutoapApplication.class, args);

        ValidationAgent validationAgent = context.getBean(ValidationAgent.class);

        InvoiceData validCase = new InvoiceData(
                "Acme Supplies", "INV-2001", "2026-06-10",
                new BigDecimal("1700.00"), "2026-07-10", List.of()
        );

        InvoiceData unknownVendor = new InvoiceData(
                "Totally Fake Vendor Co", "INV-9999", "2026-06-10",
                new BigDecimal("999.00"), "2026-07-10", List.of()
        );

        InvoiceData amountMismatch = new InvoiceData(
                "Acme Supplies", "INV-3003", "2026-06-10",
                new BigDecimal("50000.00"), "2026-07-10", List.of()
        );

        InvoiceData highValue = new InvoiceData(
                "Acme Supplies", "INV-7000", "2026-06-10",
                new BigDecimal("7000.00"), "2026-07-10", List.of()
        );
        System.out.println("=== Case 1: Valid ===");
        try {
            ValidationResult r1 = validationAgent.validate(validCase);
            System.out.println(r1);
        } catch (Exception e) {
            System.out.println("FAILED: " + e.getMessage());
        }

        System.out.println("=== Case 2: Unknown vendor ===");
        try {
            ValidationResult r2 = validationAgent.validate(unknownVendor);
            System.out.println(r2);
        } catch (Exception e) {
            System.out.println("FAILED: " + e.getMessage());
        }

        System.out.println("=== Case 3: Amount mismatch ===");
        try {
            ValidationResult r3 = validationAgent.validate(amountMismatch);
            System.out.println(r3);
        } catch (Exception e) {
            System.out.println("FAILED: " + e.getMessage());
        }

        System.out.println("=== Case 4: Exceeds threshold ===");
        try {
            ValidationResult r4 = validationAgent.validate(highValue);
            System.out.println(r4);
        } catch (Exception e) {
            System.out.println("FAILED: " + e.getMessage());
        }

        context.close();
    }
}