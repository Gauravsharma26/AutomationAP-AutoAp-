package com.gaurav.autoap.service;


import com.gaurav.autoap.model.PurchaseOrder;
import com.gaurav.autoap.model.Vendor;
import com.gaurav.autoap.repository.PurchaseOrderRepository;
import com.gaurav.autoap.repository.VendorRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
public class DataSeeder implements CommandLineRunner {

    private final VendorRepository vendorRepo;
    private final PurchaseOrderRepository poRepo;

    public DataSeeder(VendorRepository vendorRepo, PurchaseOrderRepository poRepo) {
        this.vendorRepo = vendorRepo;
        this.poRepo = poRepo;
    }

    @Override
    public void run(String... args) {
        if (vendorRepo.count() > 0) return;

        Vendor v1 = vendorRepo.save(new Vendor("Acme Supplies", "billing@acme.com", "IBAN123"));
        Vendor v2 = vendorRepo.save(new Vendor("Bright Logistics", "ap@brightlog.com", "IBAN456"));

        poRepo.save(new PurchaseOrder("PO-1001", v1, new BigDecimal("2000.00"), LocalDate.of(2026, 6, 1)));
        poRepo.save(new PurchaseOrder("PO-1002", v2, new BigDecimal("4500.00"), LocalDate.of(2026, 6, 5)));
        poRepo.save(new PurchaseOrder("PO-1700", v1, new BigDecimal("1700.00"), LocalDate.of(2026, 6, 8))); // matches invoice1
        poRepo.save(new PurchaseOrder("PO-9999", v1, new BigDecimal("500.00"), LocalDate.of(2026, 6, 8)));  // mismatch case

        System.out.println("Seed data loaded: " + vendorRepo.count() + " vendors, " + poRepo.count() + " POs.");
    }
}