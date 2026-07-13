package com.gaurav.autoap.model;


public record ExtractionOutcome(
        boolean success,
        InvoiceData data,
        String errorMessage
) {
    public static ExtractionOutcome ok(InvoiceData data) {
        return new ExtractionOutcome(true, data, null);
    }

    public static ExtractionOutcome failed(String errorMessage) {
        return new ExtractionOutcome(false, null, errorMessage);
    }
}