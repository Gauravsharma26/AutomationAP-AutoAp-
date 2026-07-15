package com.gaurav.autoap.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.math.BigDecimal;

@Configuration
@PropertySource("classpath:policy.properties")
public class PolicyConfig {

    @Value("${policy.approval.threshold}")
    private BigDecimal approvalThreshold;

    @Value("${policy.vendor.whitelist.enabled}")
    private boolean whitelistEnabled;

    public BigDecimal getApprovalThreshold() {
        return approvalThreshold;
    }

    public boolean isWhitelistEnabled() {
        return whitelistEnabled;
    }
}
