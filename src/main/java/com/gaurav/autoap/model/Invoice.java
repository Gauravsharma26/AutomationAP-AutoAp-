package com.gaurav.autoap.model;

import jakarta.persistence.*;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,unique=true)
    private String invoiceNumber;

    @Setter
    @ManyToOne
    @JoinColumn(name="vendor_id")
    private Vendor vendor;

    @ManyToOne
    @JoinColumn(name="po_id")
    private PurchaseOrder purchaseOrder;

    @Setter
    private BigDecimal amount;
    @Setter
    private LocalDate invoiceDate;
    @Setter
    private LocalDate dueDate;
    @Setter
    private String status;

    @Setter
    @Lob
    private String extractedDataJson;

    public Invoice() {}

    // Getters and setters
    public Long getId() { return id; }
    public String getInvoiceNumber() { return invoiceNumber; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }
    public Vendor getVendor() { return vendor; }

    public PurchaseOrder getPurchaseOrder() { return purchaseOrder; }
    public void setPurchaseOrder(PurchaseOrder purchaseOrder) { this.purchaseOrder = purchaseOrder; }
    public BigDecimal getAmount() { return amount; }

    public LocalDate getInvoiceDate() { return invoiceDate; }

    public LocalDate getDueDate() { return dueDate; }

    public String getStatus() { return status; }

    public String getExtractedDataJson() { return extractedDataJson; }


}
