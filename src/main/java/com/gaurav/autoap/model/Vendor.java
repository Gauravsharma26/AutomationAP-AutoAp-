package com.gaurav.autoap.model;

import jakarta.persistence.*;

@Entity
@Table(name = "vendor_name")
public class Vendor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,unique = true)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;
    private String paymentInfo;

    public Vendor(){

    }
    public Vendor(String name,String email,String paymentInfo){
        this.name=name;
        this.email=email;
        this.paymentInfo=paymentInfo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPaymentInfo() {
        return paymentInfo;
    }

    public void setPaymentInfo(String paymentInfo) {
        this.paymentInfo = paymentInfo;
    }
}
