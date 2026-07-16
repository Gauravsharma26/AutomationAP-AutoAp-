package com.gaurav.autoap.playground;


import com.gaurav.autoap.AutoapApplication;
import com.gaurav.autoap.agent.ExplanationAgent;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

public class ExplanationAgentTest {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(AutoapApplication.class, args);
        ExplanationAgent agent = context.getBean(ExplanationAgent.class);

        String[] testInputs = {
                "Status: APPROVE. Reason: Purchase order matched and all checks passed - auto-approved.",
                "Status: ESCALATE. Reason: Vendor not recognized in system - requires manual review.",
                "Status: ESCALATE. Reason: Amount exceeds approval threshold - requires manager sign-off.",
                "Status: REJECT. Reason: Duplicate invoice detected."
        };

        for (String input : testInputs) {
            System.out.println("=== Input: " + input + " ===");
            try {
                String explanation = agent.explain(input);
                System.out.println("Explanation: " + explanation);
            } catch (Exception e) {
                System.out.println("FAILED: " + e.getMessage());
            }
            System.out.println();
        }

        context.close();
    }
}