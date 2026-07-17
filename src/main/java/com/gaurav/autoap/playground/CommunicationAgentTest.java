package com.gaurav.autoap.playground;


import com.gaurav.autoap.AutoapApplication;
import com.gaurav.autoap.agent.CommunicationAgent;
import com.gaurav.autoap.model.Email;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

public class CommunicationAgentTest {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(AutoapApplication.class, args);
        CommunicationAgent agent = context.getBean(CommunicationAgent.class);

        String[] testInputs = {
                "Vendor: Bright Logistics Inc. Invoice Number: BL-8842. Amount: $4500.00. "
                        + "Status: ESCALATE. Reason: Vendor not recognized in system - requires manual review.",

                "Vendor: Acme Supplies. Invoice Number: INV-3003. Amount: $299.00. "
                        + "Status: ESCALATE. Reason: No matching purchase order found for this amount - requires manual review.",

                "Vendor: Acme Supplies. Invoice Number: INV-7000. Amount: $7000.00. "
                        + "Status: ESCALATE. Reason: Amount exceeds approval threshold - requires manager sign-off."
        };

        for (String input : testInputs) {
            System.out.println("=== Input: " + input + " ===");
            try {
                Email email = agent.draftEmail(input);
                System.out.println("To: " + email.to());
                System.out.println("Subject: " + email.subject());
                System.out.println("Body: " + email.body());
            } catch (Exception e) {
                System.out.println("FAILED: " + e.getMessage());
            }
            System.out.println();
        }

        context.close();
    }
}