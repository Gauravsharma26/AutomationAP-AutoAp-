package com.gaurav.autoap.model;



public record Email(
        String to,
        String subject,
        String body
) {}