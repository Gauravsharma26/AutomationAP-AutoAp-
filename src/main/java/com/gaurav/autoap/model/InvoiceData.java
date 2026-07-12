package com.gaurav.autoap.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record InvoiceData (
    String vendorName,
    String invoiceNumber,
    String invoiceDate,
    BigDecimal amount,
    String dueDate,
    List<LineItem> lineItems
){}
