package com.gaurav.autoap.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long invoiceId;
    private String decision;

    @Lob
    private String reason;

    private LocalDateTime timestamp;

    public AuditLog() {}

    public AuditLog(Long invoiceId, String decision, String reason) {
        this.invoiceId = invoiceId;
        this.decision = decision;
        this.reason = reason;
        this.timestamp = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public Long getInvoiceId() { return invoiceId; }
    public String getDecision() { return decision; }
    public String getReason() { return reason; }
    public LocalDateTime getTimestamp() { return timestamp; }
}