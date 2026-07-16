package com.gaurav.autoap.service;


import com.gaurav.autoap.agent.ExtractionAgent;
import com.gaurav.autoap.agent.ValidationAgent;
import com.gaurav.autoap.model.*;
import com.gaurav.autoap.service.AuditService;
import com.gaurav.autoap.service.StubDecisionService;
import com.gaurav.autoap.service.StubValidationService;
import org.springframework.stereotype.Service;

@Service
public class InvoiceOrchestrator {

    private final ExtractionAgent extractionAgent;
    private final RuleBasedValidationService validationService;
    private final AuditService auditService;
    private final RuleBasedDecisionService decisionService;

    public InvoiceOrchestrator(
            ExtractionAgent extractionAgent,
            RuleBasedValidationService validationService,
            AuditService auditService,
            RuleBasedDecisionService decisionService
    ) {
        this.extractionAgent = extractionAgent;
        this.validationService = validationService;
        this.auditService = auditService;
        this.decisionService=decisionService;
    }

    public InvoiceCase processInvoice(String rawText) {
        InvoiceCase invoiceCase = new InvoiceCase(rawText);

        // --- Extraction step (with safe wrapper, per Part B pattern) ---
        ExtractionOutcome outcome = safeExtract(rawText);
        invoiceCase.setExtractionOutcome(outcome);

        if (!outcome.success()) {
            // Extraction failed -> escalate immediately, skip remaining steps
            Decision escalation = new Decision(DecisionStatus.ESCALATE, "Extraction failed: " + outcome.errorMessage());
            invoiceCase.setDecision(escalation);
            auditService.log(null, escalation.status().toString(), escalation.reason());
            return invoiceCase;
        }

        InvoiceData invoiceData = outcome.data();
        if (invoiceData.vendorName() == null && invoiceData.invoiceNumber() == null && invoiceData.amount() == null) {
            Decision escalation = new Decision(DecisionStatus.ESCALATE, "Extraction returned no usable data — likely not a valid invoice");
            invoiceCase.setDecision(escalation);
            auditService.log(null, escalation.status().toString(), escalation.reason());
            return invoiceCase;
        }

        // --- Validation step (stubbed for now) ---
        ValidationResult validationResult = validationService.validate(invoiceData);
        invoiceCase.setValidationResult(validationResult);

        // --- Decision step (stubbed for now) ---
        Decision decision = decisionService.decide(validationResult);
        invoiceCase.setDecision(decision);

        // --- Communication step (skipped today - Day 9) ---
        if (decision.status() != DecisionStatus.APPROVE) {
            invoiceCase.setEmailDraft("(Email drafting not yet implemented - placeholder)");
        }

        // --- Audit step (real, always runs) ---
        auditService.log(null, decision.status().toString(), decision.reason());

        return invoiceCase;
    }

    private ExtractionOutcome safeExtract(String rawText) {
        try {
            InvoiceData data = extractionAgent.extract(rawText);
            return ExtractionOutcome.ok(data);
        } catch (Exception e) {
            return ExtractionOutcome.failed(e.getMessage());
        }
    }
}