package com.gaurav.autoap.controller;


import com.gaurav.autoap.model.InvoiceCase;
import com.gaurav.autoap.service.InvoiceOrchestrator;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    private final InvoiceOrchestrator orchestrator;

    public InvoiceController(InvoiceOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    @PostMapping
    public InvoiceCase processInvoice(@RequestBody String invoiceText) {
        return orchestrator.processInvoice(invoiceText);
    }
}