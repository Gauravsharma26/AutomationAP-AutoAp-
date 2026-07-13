package com.gaurav.autoap.service;

import com.gaurav.autoap.model.AuditLog;
import com.gaurav.autoap.repository.AuditLogRepository;
import org.springframework.stereotype.Service;

@Service
public class AuditService {
    private final AuditLogRepository auditLogRepository;

    public AuditService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }
    public void log(Long invoiceId, String decision, String reason) {
        AuditLog entry = new AuditLog(invoiceId, decision, reason);
        auditLogRepository.save(entry);
        System.out.println("AUDIT LOG: invoice=" + invoiceId + " decision=" + decision + " reason=" + reason);
    }




}
