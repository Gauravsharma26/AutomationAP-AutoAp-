package com.gaurav.autoap.service;

import com.gaurav.autoap.config.PolicyConfig;
import com.gaurav.autoap.model.InvoiceData;
import com.gaurav.autoap.model.ValidationResult;
import com.gaurav.autoap.repository.PurchaseOrderRepository;
import com.gaurav.autoap.repository.VendorRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RuleBasedValidationService {

    private final VendorRepository vendorRepository;
    private final PurchaseOrderRepository poRepository;
    private final PolicyConfig policyConfig;

    public RuleBasedValidationService(VendorRepository vendorRepository,
                                      PurchaseOrderRepository poRepository,
                                      PolicyConfig policyConfig) {
        this.vendorRepository = vendorRepository;
        this.poRepository = poRepository;
        this.policyConfig = policyConfig;
    }

    public ValidationResult validate(InvoiceData invoiceData) {
        List<String> issues = new ArrayList<>();

        boolean vendorExists = invoiceData.vendorName() != null
                && vendorRepository.findByName(invoiceData.vendorName()) != null;

        boolean poMatch = false;

        if (!vendorExists) {
            issues.add("Vendor not found in system");
        } else if (invoiceData.amount() != null) {
            poMatch = poRepository.findAll().stream()
                    .anyMatch(po -> po.getVendor().getName().equalsIgnoreCase(invoiceData.vendorName())
                            && po.getAmount().compareTo(invoiceData.amount()) == 0);

            if (!poMatch) {
                issues.add("No matching purchase order for this amount");
            }
        }

        if (invoiceData.amount() != null
                && invoiceData.amount().compareTo(policyConfig.getApprovalThreshold()) > 0) {
            issues.add("Amount exceeds approval threshold - requires manager sign-off");
        }

        return new ValidationResult(poMatch, false, issues);
    }
}