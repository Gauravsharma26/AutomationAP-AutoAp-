package com.gaurav.autoap.controller;

import com.gaurav.autoap.model.Invoice;
import com.gaurav.autoap.model.InvoiceCase;
import com.gaurav.autoap.repository.InvoiceRepository;
import com.gaurav.autoap.service.InvoiceOrchestrator;
import com.gaurav.autoap.service.PdfExtractionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    private final InvoiceOrchestrator orchestrator;
    private final PdfExtractionService pdfExtractionService;
    private final InvoiceRepository invoiceRepository;

    public InvoiceController(InvoiceOrchestrator orchestrator, PdfExtractionService pdfExtractionService, InvoiceRepository invoiceRepository) {
        this.orchestrator = orchestrator;
        this.pdfExtractionService = pdfExtractionService;
        this.invoiceRepository=invoiceRepository;
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

            return ResponseEntity.ok(Map.of(
                    "total", total,
                    "approved", approved,
                    "escalated", escalated,
                    "rejected", rejected
            ));
        }
}