package com.gaurav.autoap.tool;

import com.gaurav.autoap.config.PolicyConfig;
import com.gaurav.autoap.model.PurchaseOrder;
import com.gaurav.autoap.repository.PurchaseOrderRepository;
import com.gaurav.autoap.repository.VendorRepository;
import dev.langchain4j.agent.tool.Tool;
import org.springframework.stereotype.Component;

@Component
public class InvoiceTools {

    private final PurchaseOrderRepository poRepository;
    private final VendorRepository vendorRepository;
    private final PolicyConfig policyConfig;

    public InvoiceTools(PurchaseOrderRepository poRepository, VendorRepository vendorRepository,PolicyConfig policyConfig) {
        this.poRepository = poRepository;
        this.vendorRepository = vendorRepository;
        this.policyConfig=policyConfig;
    }

    @Tool("Check whether a vendor with the given name exists in the system")
    public boolean vendorExists(String vendorName){
        if(vendorName== null)return false;
        return vendorRepository.findByName(vendorName)!=null;
    }

    @Tool("Find a purchase order by its PO number and return its amount and vendor name. Returns 'NOT_FOUND' if it doesn't exist.")
    public String findPurchaseOrder(String poNumber){
        PurchaseOrder po=poRepository.findByPoNumber(poNumber);
        if(po==null){
            return "NOT_FOUND";
        }
        return "PO " + po.getPoNumber() + ": vendor=" + po.getVendor().getName() + ", amount=" + po.getAmount();
    }
    @Tool("Find any purchase order belonging to a given vendor with a matching amount. Returns the PO number if found, otherwise 'NOT_FOUND'.")
    public String findMatchingPurchaseOrderForVendor(String vendorName, double amount) {
        return poRepository.findAll().stream()
                .filter(po -> po.getVendor().getName().equalsIgnoreCase(vendorName))
                .filter(po -> po.getAmount().doubleValue() == amount)
                .findFirst()
                .map(PurchaseOrder::getPoNumber)
                .orElse("NOT_FOUND");
    }
    @Tool("Check whether an invoice amount exceeds the company's approval threshold and requires manager sign-off. Returns 'REQUIRES_APPROVAL' or 'WITHIN_LIMIT'.")
    public String checkApprovalThreshold(double amount) {
        if (amount > policyConfig.getApprovalThreshold().doubleValue()) {
            return "REQUIRES_APPROVAL";
        }
        return "WITHIN_LIMIT";
    }


}
