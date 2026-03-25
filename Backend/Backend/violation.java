package com.example.traffic.model;

import jakarta.persistence.*;

@Entity
public class violation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // VEHICLE NUMBER (IMPORTANT FOR SEARCH)
    private String vehicleNumber;

    private String violationType;

    private double fineAmount;

    // IMAGE NAME (for uploaded evidence)
    private String imageName;

    // PAYMENT STATUS
    private boolean paid = false;

    // ===== GETTERS =====

    public Long getId() {
        return id;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public String getViolationType() {
        return violationType;
    }

    public double getFineAmount() {
        return fineAmount;
    }

    public String getImageName() {
        return imageName;
    }

    public boolean isPaid() {
        return paid;
    }

    // ===== SETTERS =====

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public void setViolationType(String violationType) {
        this.violationType = violationType;
    }

    public void setFineAmount(double fineAmount) {
        this.fineAmount = fineAmount;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }
}