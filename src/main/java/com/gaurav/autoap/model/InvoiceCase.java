package com.gaurav.autoap.model;

public class InvoiceCase {
    //Using a plain class here since it gets mutated , as it flows through the pipeline
    private String rawText;
    private ExtractionOutcome extractionOutcome;
    private ValidationResult validationResult;
    private Decision decision;
    private String emailDraft;

    public InvoiceCase(String rawText) {
        this.rawText = rawText;
    }

    public String getRawText() {
        return rawText;
    }

    public ExtractionOutcome getExtractionOutcome() {
        return extractionOutcome;
    }

    public ValidationResult getValidationResult() {
        return validationResult;
    }

    public Decision getDecision() {
        return decision;
    }

    public String getEmailDraft() {
        return emailDraft;
    }

    public void setExtractionOutcome(ExtractionOutcome extractionOutcome) {
        this.extractionOutcome = extractionOutcome;
    }

    public void setValidationResult(ValidationResult validationResult) {
        this.validationResult = validationResult;
    }

    public void setDecision(Decision decision) {
        this.decision = decision;
    }

    public void setEmailDraft(String emailDraft) {
        this.emailDraft = emailDraft;
    }
}
