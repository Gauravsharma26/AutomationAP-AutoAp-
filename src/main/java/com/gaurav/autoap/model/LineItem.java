package com.gaurav.autoap.model;

import java.math.BigDecimal;

public record LineItem(
        String description,
        int quantity,
        BigDecimal unitPrice
) {
}
