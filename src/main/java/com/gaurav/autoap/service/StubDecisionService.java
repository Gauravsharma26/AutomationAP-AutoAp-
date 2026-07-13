package com.gaurav.autoap.service;

import com.gaurav.autoap.model.Decision;
import com.gaurav.autoap.model.DecisionStatus;
import com.gaurav.autoap.model.ValidationResult;
import org.springframework.stereotype.Service;

@Service
public class StubDecisionService {

    public Decision decide(ValidationResult validationResult) {
        // Hardcoded stub logic - replaced with real DecisionAgent later
        if (validationResult.duplicate()) {
            return new Decision(DecisionStatus.REJECT, "Stub: duplicate detected");
        }
        if (validationResult.poMatch()) {
            return new Decision(DecisionStatus.APPROVE, "Stub: PO matched, auto-approved");
        }
        return new Decision(DecisionStatus.ESCALATE, "Stub: no PO match, escalating");
    }
}