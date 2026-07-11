package com.gaurav.autoap.repository;


import com.gaurav.autoap.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findByInvoiceId(Long invoiceId);

}