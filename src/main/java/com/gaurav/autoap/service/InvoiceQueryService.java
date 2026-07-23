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

        List<Invoice> filtered = all.stream()
                .filter(inv -> intent.vendorName() == null
                        || (inv.getVendorName() != null
                        && inv.getVendorName().toLowerCase().contains(intent.vendorName().toLowerCase())))
                .filter(inv -> intent.status() == null || intent.status().equals(inv.getStatus()))
                .collect(Collectors.toList());
        if ("total_amount".equals(intent.metric())) {
            BigDecimal total = filtered.stream()
                    .map(Invoice::getAmount)
                    .filter(a -> a != null)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            return buildFactString(intent, filtered.size(), total);
        } else {
            return buildFactString(intent, filtered.size(), null);
        }
    }

    private String buildFactString(QueryIntent intent, int count, BigDecimal total) {
        StringBuilder facts = new StringBuilder();
        facts.append("Query filters: ");
        facts.append("vendor=").append(intent.vendorName() != null ? intent.vendorName() : "any").append(", ");
        facts.append("status=").append(intent.status() != null ? intent.status() : "any").append(". ");
        facts.append("Result: count=").append(count);
        if (total != null) {
            facts.append(", total_amount=$").append(String.format("%.2f", total));
        }
        return facts.toString();
    }
}