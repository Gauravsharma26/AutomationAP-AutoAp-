package com.gaurav.autoap.model;

public record QueryIntent(
        String vendorName,
        String status,
        String metric
) {}