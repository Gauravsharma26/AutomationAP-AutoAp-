package com.gaurav.autoap.repository;

import com.gaurav.autoap.model.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;

public  interface VendorRepository extends JpaRepository<Vendor,Long> {

    Vendor findByName(String name);
}
