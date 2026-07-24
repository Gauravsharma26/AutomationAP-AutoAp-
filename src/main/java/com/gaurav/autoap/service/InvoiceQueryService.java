package com.gaurav.autoap.service;


import com.gaurav.autoap.model.Invoice;
import com.gaurav.autoap.model.QueryIntent;
import com.gaurav.autoap.repository.InvoiceRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InvoiceQueryService {

    private final InvoiceRepository invoiceRepository;

    public InvoiceQueryService(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    public String executeQuery(QueryIntent intent) {
        List<Invoice> all = invoiceRepository.findAll();
        String normalizedStatus = normalizeStatus(intent.status());

        List<Invoice> filtered = all.stream()
                .filter(inv -> intent.vendorName() == null
                        || (inv.getVendorName() != null
                        && inv.getVendorName().toLowerCase().contains(intent.vendorName().toLowerCase())))
                .filter(inv -> normalizedStatus == null || normalizedStatus.equals(inv.getStatus()))
                .collect(Collectors.toList());

        if ("total_amount".equals(intent.metric())) {
            BigDecimal total = filtered.stream()
                    .map(Invoice::getAmount)
                    .filter(a -> a != null)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            return buildFactString(intent, normalizedStatus, filtered.size(), total);
        } else {
            return buildFactString(intent, normalizedStatus, filtered.size(), null);
        }
    }

    private String normalizeStatus(String status) {
        if (status == null) return null;
        String s = status.trim().toUpperCase();
        if (s.startsWith("APPROV")) return "APPROVE";
        if (s.startsWith("ESCALAT")) return "ESCALATE";
        if (s.startsWith("REJECT")) return "REJECT";
        return s;
    }

    private String buildFactString(QueryIntent intent, String normalizedStatus, int count, BigDecimal total) {
        StringBuilder facts = new StringBuilder();
        facts.append("vendor_filter=").append(intent.vendorName() != null ? intent.vendorName() : "none").append(", ");
        facts.append("status_filter=").append(normalizedStatus != null ? normalizedStatus : "none").append(". ");
        facts.append("count=").append(count);
        if (total != null) {
            facts.append(", total_amount=").append(String.format("%.2f", total));
        }
        return facts.toString();
    }
}