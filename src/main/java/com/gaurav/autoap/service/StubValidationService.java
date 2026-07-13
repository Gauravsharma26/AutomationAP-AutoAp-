package com.gaurav.autoap.service;

import com.gaurav.autoap.model.InvoiceData;
import com.gaurav.autoap.model.ValidationResult;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StubValidationService {

    public ValidationResult validate(InvoiceData invoiceData) {
        // Hardcoded stub logic - replaced with real ValidationAgent later
        return new ValidationResult(true, false, List.of());
    }
}
