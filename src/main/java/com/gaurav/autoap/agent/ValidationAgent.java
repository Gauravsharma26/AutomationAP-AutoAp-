package com.gaurav.autoap.agent;

import com.gaurav.autoap.model.InvoiceData;
import com.gaurav.autoap.model.ValidationResult;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

// EXPLORATION NOTE: This LLM tool-calling approach was tested extensively across
// 7 distinct prompt strategies (rule tables, chain-of-thought restating, format
// constraints, structured reasoning fields, simplified step-gating) and 2 different
// models (Llama 3.2:3b, Qwen2.5:3b). Each fix resolved one failing test case while
// introducing a regression in a previously-correct case - a repeatable pattern
// indicating a genuine reliability ceiling for 3-4B models on multi-fact numeric
// synthesis, not a prompting gap. Production validation logic uses
// RuleBasedValidationService (deterministic Java) instead. Retained here to
// demonstrate agentic tool-calling design and the evaluation process behind
// this architectural decision.

public interface ValidationAgent {

    @SystemMessage("""
        Call vendorExists once. Call findMatchingPurchaseOrderForVendor once. 
        Call checkApprovalThreshold once. Call each tool exactly one time, then 
        stop calling tools and answer immediately.

        Then follow these steps in order:

        Step 1: If vendorExists is false, set poMatch to false and issues to 
        ["Vendor not found in system"]. Stop here, skip Step 2.

        Step 2: If vendorExists is true, check findMatchingPurchaseOrderForVendor. 
        If it is NOT_FOUND, set poMatch to false and issues to ["No matching 
        purchase order for this amount"]. If it found a PO number, set poMatch 
        to true and issues to an empty list.
        Only set poMatch to true if findMatchingPurchaseOrderForVendor returned an actual\s
        PO number and not the text NOT_FOUND. If it returned NOT_FOUND for any reason,\s
        poMatch must be false, even if vendorExists is true.

        Step 3: If checkApprovalThreshold is REQUIRES_APPROVAL, add "Amount 
        exceeds approval threshold - requires manager sign-off" to the issues 
        list from Step 1 or 2. If it is WITHIN_LIMIT, do not change issues.

        Set duplicate to false always.

        Answer with only this JSON object, nothing else:
        {"poMatch": true or false, "duplicate": false, "issues": [...]}

        Example: {"poMatch": true, "duplicate": false, "issues": []}
        Example: {"poMatch": false, "duplicate": false, "issues": ["Vendor not found in system"]}
        """)
    ValidationResult validate(@UserMessage InvoiceData invoiceData);
}