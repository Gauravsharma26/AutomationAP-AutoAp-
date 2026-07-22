package com.gaurav.autoap.repository;

import com.gaurav.autoap.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvoiceRepository extends JpaRepository<Invoice,Long> {
    Invoice findByInvoiceNumber(String invoiceNumber);
    List<Invoice> findAllByOrderByProcessedAtDesc();
    List<Invoice> findByInvoiceNumberAndVendorName(String invoiceNumber, String vendorName);
    List<Invoice> findByStatusAndHumanDecisionOrderByProcessedAtDesc(String status, String humanDecision);
}
