package com.gaurav.autoap.agent;


import com.gaurav.autoap.model.QueryIntent;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

public interface QueryIntentAgent {

    @SystemMessage("""
        You are a query interpreter for an invoice processing system. Given a 
        natural-language question, extract the following fields as a JSON object:

        - vendorName: the vendor name mentioned in the question, or null if none is mentioned.
        - status: one of "APPROVE", "ESCALATE", "REJECT", or null if the question 
          doesn't ask about a specific status. Map words like "approved" to APPROVE, 
          "escalated" or "flagged" to ESCALATE, "rejected" to REJECT.
        - metric: "count" if the question asks how many invoices, or "total_amount" 
          if the question asks about a dollar amount/total/sum. Default to "count" 
          if unclear.

        Only extract information explicitly present in the question. Do not guess 
        vendor names that aren't mentioned.

        Example: "How many invoices from Acme Supplies were escalated?"
        Output: {"vendorName": "Acme Supplies", "status": "ESCALATE", "metric": "count"}

        Example: "What's our total approved amount?"
        Output: {"vendorName": null, "status": "APPROVE", "metric": "total_amount"}

        Example: "How many invoices have been processed?"
        Output: {"vendorName": null, "status": null, "metric": "count"}

        Return only the JSON object, nothing else.
        """)
    QueryIntent parseQuestion(@UserMessage String question);
}