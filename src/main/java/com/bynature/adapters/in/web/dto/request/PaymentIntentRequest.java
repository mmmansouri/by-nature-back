package com.bynature.adapters.in.web.dto.request;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;


public class PaymentIntentRequest {

    @NotNull
    @Min(4)
    private Long amount;

    @Email
    private String email;

    @NotBlank
    private String customerId;

    @NotBlank
    private String phone;

    @NotBlank
    private String state;

    @NotBlank
    @Size(min = 5, max = 200)
    private String productName;

    @NotBlank
    private UUID orderId;

    public PaymentIntentRequest(Long amount, String email, String productName, UUID orderId, String customerId,
                                String phone, String state) {
        this.amount = amount;
        this.email = email;
        this.productName = productName;
        this.orderId = orderId;
        this.customerId = customerId;
        this.phone = phone;
        this.state = state;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
