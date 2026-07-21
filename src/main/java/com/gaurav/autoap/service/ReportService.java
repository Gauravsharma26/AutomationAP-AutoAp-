package com.gaurav.autoap.service;


import com.gaurav.autoap.model.Invoice;
import com.gaurav.autoap.repository.InvoiceRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReportService {

    private final InvoiceRepository invoiceRepository;

    public ReportService(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    public String generateSummaryReport() {
        List<Invoice> all = invoiceRepository.findAllByOrderByProcessedAtDesc();

        List<Invoice> approved = all.stream().filter(i -> "APPROVE".equals(i.getStatus())).collect(Collectors.toList());
        List<Invoice> escalated = all.stream().filter(i -> "ESCALATE".equals(i.getStatus())).collect(Collectors.toList());
        List<Invoice> rejected = all.stream().filter(i -> "REJECT".equals(i.getStatus())).collect(Collectors.toList());

        BigDecimal approvedTotal = approved.stream()
                .map(Invoice::getAmount)
                .filter(a -> a != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal needsAttentionTotal = java.util.stream.Stream.concat(escalated.stream(), rejected.stream())
                .map(Invoice::getAmount)
                .filter(a -> a != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        StringBuilder report = new StringBuilder();
        report.append("AutoAP Invoice Processing Summary\n");
        report.append("====================================\n\n");
        report.append(String.format("Total invoices processed: %d%n", all.size()));
        report.append(String.format("Approved: %d (total $%.2f)%n", approved.size(), approvedTotal));
        report.append(String.format("Escalated: %d%n", escalated.size()));
        report.append(String.format("Rejected: %d%n", rejected.size()));
        report.append(String.format("Total amount requiring attention: $%.2f%n%n", needsAttentionTotal));

        if (!escalated.isEmpty() || !rejected.isEmpty()) {
            report.append("Items requiring attention:\n");
            report.append("---------------------------\n");
            for (Invoice inv : escalated) {
                report.append(String.format("- [ESCALATED] %s - %s - $%.2f - %s%n",
                        inv.getInvoiceNumber(), inv.getVendorName(), inv.getAmount(), inv.getReason()));
            }
            for (Invoice inv : rejected) {
                report.append(String.format("- [REJECTED] %s - %s - $%.2f - %s%n",
                        inv.getInvoiceNumber(), inv.getVendorName(), inv.getAmount(), inv.getReason()));
            }
        } else {
            report.append("No items require attention - all invoices processed cleanly.\n");
        }

        return report.toString();
    }
}