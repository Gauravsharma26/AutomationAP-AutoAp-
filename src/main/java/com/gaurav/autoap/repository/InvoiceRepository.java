package com.gaurav.autoap.repository;

import com.gaurav.autoap.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceRepository extends JpaRepository<Invoice,Long> {
    Invoice findByInvoiceNumber(String invoiceNumber);
}
