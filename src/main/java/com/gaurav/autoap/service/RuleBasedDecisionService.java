package com.gaurav.autoap.service;


import com.gaurav.autoap.model.Decision;
import com.gaurav.autoap.model.DecisionStatus;
import com.gaurav.autoap.model.ValidationResult;
import org.springframework.stereotype.Service;

@Service
public class RuleBasedDecisionService {

    public Decision decide(ValidationResult validationResult) {

        if (validationResult.duplicate()) {
            return new Decision(DecisionStatus.REJECT, "Duplicate invoice detected");
        }

        boolean vendorMissing = validationResult.issues().contains("Vendor not found in system");
        boolean poMismatch = validationResult.issues().contains("No matching purchase order for this amount");
        boolean overThreshold = validationResult.issues().contains("Amount exceeds approval threshold - requires manager sign-off");

        if (vendorMissing) {
            return new Decision(DecisionStatus.ESCALATE, "Vendor not recognized in system - requires manual review");
        }

        if (poMismatch) {
            return new Decision(DecisionStatus.ESCALATE, "No matching purchase order found for this amount - requires manual review");
        }

        if (overThreshold) {
            return new Decision(DecisionStatus.ESCALATE, "Amount exceeds approval threshold - requires manager sign-off");
        }

        if (validationResult.poMatch()) {
            return new Decision(DecisionStatus.APPROVE, "Purchase order matched and all checks passed - auto-approved");
        }

        // Fallback safety net - shouldn't normally reach here, but escalate rather than guess
        return new Decision(DecisionStatus.ESCALATE, "Validation result did not clearly indicate approval - requires manual review");
    }
}
