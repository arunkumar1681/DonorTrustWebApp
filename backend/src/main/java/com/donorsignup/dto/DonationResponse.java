package com.donorsignup.dto;

import com.donorsignup.model.Donation;

public class DonationResponse {
    private Long id;
    private String donationType;
    private String description;
    private String quantity;
    private String location;
    private String photoUrl;
    private String donorName;
    private boolean status;
    private Long trustId;
    private String trustName;

    public DonationResponse(Donation donation) {
        this.id = donation.getId();
        this.donationType = donation.getDonationType();
        this.description = donation.getDescription();
        this.quantity = donation.getQuantity();
        this.location = donation.getLocation();
        this.photoUrl = donation.getPhotoUrl();
        this.donorName = donation.getDonor() != null ? donation.getDonor().getName() : "Unknown Donor";
        this.status = donation.isStatus();
        this.trustId = donation.getTrustId();
        this.trustName = donation.getTrustName();
    }

    public Long getId() { return id; }
    public String getDonationType() { return donationType; }
    public String getDescription() { return description; }
    public String getQuantity() { return quantity; }
    public String getLocation() { return location; }
    public String getPhotoUrl() { return photoUrl; }
    public String getDonorName() { return donorName; }
    public boolean isStatus() { return status; }
    public Long getTrustId() { return trustId; }
    public String getTrustName() { return trustName; }
}



