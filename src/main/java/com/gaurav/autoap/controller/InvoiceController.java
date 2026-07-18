package com.gaurav.autoap.controller;

import com.gaurav.autoap.model.InvoiceCase;
import com.gaurav.autoap.service.InvoiceOrchestrator;
import com.gaurav.autoap.service.PdfExtractionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    private final InvoiceOrchestrator orchestrator;
    private final PdfExtractionService pdfExtractionService;

    public InvoiceController(InvoiceOrchestrator orchestrator, PdfExtractionService pdfExtractionService) {
        this.orchestrator = orchestrator;
        this.pdfExtractionService = pdfExtractionService;
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
}