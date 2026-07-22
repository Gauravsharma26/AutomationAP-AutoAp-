package com.gaurav.autoap.controller;

import com.gaurav.autoap.model.Invoice;
import com.gaurav.autoap.model.InvoiceCase;
import com.gaurav.autoap.repository.InvoiceRepository;
import com.gaurav.autoap.service.AuditService;
import com.gaurav.autoap.service.EmailSenderService;
import com.gaurav.autoap.service.InvoiceOrchestrator;
import com.gaurav.autoap.service.PdfExtractionService;
import com.gaurav.autoap.service.ReportService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.gaurav.autoap.model.ReviewRequest;
import java.time.LocalDateTime;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    private final InvoiceOrchestrator orchestrator;
    private final PdfExtractionService pdfExtractionService;
    private final InvoiceRepository invoiceRepository;
    private final ReportService reportService;
    private final EmailSenderService emailSenderService;
    private final AuditService auditService;

    public InvoiceController(InvoiceOrchestrator orchestrator, PdfExtractionService pdfExtractionService,
                             InvoiceRepository invoiceRepository, ReportService reportService,
                             EmailSenderService emailSenderService, AuditService auditService) {
        this.orchestrator = orchestrator;
        this.pdfExtractionService = pdfExtractionService;
        this.invoiceRepository = invoiceRepository;
        this.reportService = reportService;
        this.emailSenderService = emailSenderService;
        this.auditService = auditService;
    }

    @PostMapping
    public ResponseEntity<?> processInvoiceText(@RequestBody String invoiceText) {
        try {
            InvoiceCase result = orchestrator.processInvoice(invoiceText);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unexpected error processing invoice: " + e.getMessage());
        }
    }

    @PostMapping("/upload")
    public ResponseEntity<?> processInvoiceFile(@RequestParam("file") MultipartFile file) {
        try {
            String text = pdfExtractionService.extractText(file.getBytes());
            InvoiceCase result = orchestrator.processInvoice(text);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unexpected error processing invoice file: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> listInvoices() {
        return ResponseEntity.ok(invoiceRepository.findAllByOrderByProcessedAtDesc());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getInvoice(@PathVariable Long id) {
        return invoiceRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getStats() {
        List<Invoice> all = invoiceRepository.findAll();
        long total = all.size();
        long approved = all.stream().filter(i -> "APPROVE".equals(i.getStatus())).count();
        long escalated = all.stream().filter(i -> "ESCALATE".equals(i.getStatus())).count();
        long rejected = all.stream().filter(i -> "REJECT".equals(i.getStatus())).count();
        long pendingReview = all.stream()
                .filter(i -> "ESCALATE".equals(i.getStatus()) && "PENDING".equals(i.getHumanDecision()))
                .count();

        return ResponseEntity.ok(Map.of(
                "total", total,
                "approved", approved,
                "escalated", escalated,
                "rejected", rejected,
                "pendingReview", pendingReview
        ));
    }

    @PostMapping("/send-report")
    public ResponseEntity<?> sendReport() {
        try {
            String report = reportService.generateSummaryReport();
            emailSenderService.sendReport(report);
            return ResponseEntity.ok(Map.of("status", "sent", "report", report));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to send report: " + e.getMessage());
        }
    }

    @GetMapping("/pending")
    public ResponseEntity<?> getPendingReview() {
        return ResponseEntity.ok(
                invoiceRepository.findByStatusAndHumanDecisionOrderByProcessedAtDesc("ESCALATE", "PENDING")
        );
    }

    @PostMapping("/{id}/review")
    public ResponseEntity<?> reviewInvoice(@PathVariable Long id, @RequestBody ReviewRequest request) {
        return invoiceRepository.findById(id)
                .map(invoice -> {
                    String decision = request.decision().toUpperCase();
                    if (!decision.equals("APPROVE") && !decision.equals("REJECT")) {
                        return ResponseEntity.badRequest().body("decision must be APPROVE or REJECT");
                    }

                    invoice.setHumanDecision(decision.equals("APPROVE") ? "APPROVED" : "REJECTED");
                    invoice.setReviewerNote(request.reviewerNote());
                    invoice.setReviewedAt(LocalDateTime.now());
                    invoiceRepository.save(invoice);

                    auditService.log(
                            invoice.getId(),
                            "HUMAN_" + decision,
                            "Manually reviewed: " + (request.reviewerNote() != null ? request.reviewerNote() : "no note provided")
                    );

                    return ResponseEntity.ok(invoice);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}