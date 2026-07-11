package com.gaurav.autoap.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
public class PurchaseOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,unique = true)
    private String poNumber;

    @ManyToOne
    @JoinColumn(name = "vendor_id")
    private Vendor vendor;

    private BigDecimal amount;
    private LocalDate orderDate;
    public PurchaseOrder(){

    }

    public PurchaseOrder(String poNumber, Vendor vendor, BigDecimal amount, LocalDate orderDate) {
        this.poNumber = poNumber;
        this.vendor = vendor;
        this.amount = amount;
        this.orderDate = orderDate;
    }

    public String getPoNumber() {
        return poNumber;
    }

    public Vendor getVendor() {
        return vendor;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }
}
