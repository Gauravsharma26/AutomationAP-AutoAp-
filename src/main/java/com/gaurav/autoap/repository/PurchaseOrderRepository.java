package com.gaurav.autoap.repository;

import com.gaurav.autoap.model.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder,Long> {

    PurchaseOrder findByPoNumber(String poNumber);

}
