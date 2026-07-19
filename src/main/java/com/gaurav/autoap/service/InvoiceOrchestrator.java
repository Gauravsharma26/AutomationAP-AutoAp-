package com.gaurav.autoap.service;


import com.gaurav.autoap.agent.CommunicationAgent;
import com.gaurav.autoap.agent.ExtractionAgent;
import com.gaurav.autoap.agent.ValidationAgent;
import com.gaurav.autoap.model.*;
import com.gaurav.autoap.repository.InvoiceRepository;
import com.gaurav.autoap.service.AuditService;
import com.gaurav.autoap.service.StubDecisionService;
import com.gaurav.autoap.service.StubValidationService;
import com.gaurav.autoap.utility.DateParsingUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class InvoiceOrchestrator {

    private final ExtractionAgent extractionAgent;
    private final RuleBasedValidationService validationService;
    private final AuditService auditService;
    private final RuleBasedDecisionService decisionService;
    private final CommunicationAgent communicationAgent;
    private final InvoiceRepository invoiceRepository;

    public InvoiceOrchestrator(
            ExtractionAgent extractionAgent,
            RuleBasedValidationService validationService,
            AuditService auditService,
            RuleBasedDecisionService decisionService,
             CommunicationAgent communicationAgent,
            InvoiceRepository invoiceRepository
    ) {
        this.extractionAgent = extractionAgent;
        this.validationService = validationService;
        this.auditService = auditService;
        this.decisionService=decisionService;
        this.communicationAgent=communicationAgent;
        this.invoiceRepository=invoiceRepository;
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
            String summary = String.format(
                    "Vendor: %s. Invoice Number: %s. Amount: %s. Status: %s. Reason: %s.",
                    invoiceData.vendorName(), invoiceData.invoiceNumber(), invoiceData.amount(),
                    decision.status(), decision.reason()
            );
            try {
                Email email = communicationAgent.draftEmail(summary);
                invoiceCase.setEmailDraft(email.subject() + "\n\n" + email.body());
            } catch (Exception e) {
                invoiceCase.setEmailDraft("Email generation failed: " + e.getMessage() + " - manual email required");
            }
        }

        Invoice invoiceEntity = new Invoice();
        invoiceEntity.setInvoiceNumber(invoiceData.invoiceNumber());
        invoiceEntity.setVendorName(invoiceData.vendorName());
        invoiceEntity.setAmount(invoiceData.amount());
        invoiceEntity.setStatus(decision.status().toString());
        invoiceEntity.setReason(decision.reason());
        invoiceEntity.setEmailDraft(invoiceCase.getEmailDraft());
        invoiceEntity.setIssuesJson(String.join("; ", validationResult.issues()));
        invoiceEntity.setProcessedAt(LocalDateTime.now());

        try {
            if (invoiceData.invoiceDate() != null) {
                invoiceEntity.setInvoiceDate(DateParsingUtil.parseFlexible(invoiceData.invoiceDate()));
            }
            if (invoiceData.dueDate() != null) {
                invoiceEntity.setDueDate(DateParsingUtil.parseFlexible(invoiceData.dueDate()));
            }
        } catch (Exception ignored) {
            // dates optional for dashboard display, don't block saving the record
        }

        Invoice savedInvoice = invoiceRepository.save(invoiceEntity);

        auditService.log(savedInvoice.getId(), decision.status().toString(), decision.reason());
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