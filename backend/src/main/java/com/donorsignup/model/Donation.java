package com.donorsignup.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
@Table(name = "donations")
public class Donation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String donationType;
    private String description;
    private String quantity;
    private String location;
    private String photoUrl;  

    @Column(name = "donor_name")  
    private String donorName;

    @ManyToOne
    @JoinColumn(name = "donor_id", nullable = false)
    @JsonBackReference
    private Donor donor;
    
    @Column(name = "donor_contact_no", length = 15, nullable = false)
    private String donorContactNo; 

    @Column(name = "status", nullable = false)
    private boolean status = false; 

    @Column(name = "trust_id")
    private Long trustId; 

    @Column(name = "trust_name")
    private String trustName; 
    
    @Column(name = "trust_contact_no", length = 15)
    private String trustContactNo;  

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDonationType() { return donationType; }
    public void setDonationType(String donationType) { this.donationType = donationType; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getQuantity() { return quantity; }
    public void setQuantity(String quantity) { this.quantity = quantity; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

    public Donor getDonor() { return donor; }
    public void setDonor(Donor donor) { this.donor = donor; }

    public String getDonorName() { return donorName; }
    public void setDonorName(String donorName) { this.donorName = donorName; }

    public boolean isStatus() { return status; }
    public void setStatus(boolean status) { this.status = status; }

    public Long getTrustId() { return trustId; }
    public void setTrustId(Long trustId) { this.trustId = trustId; }

    public String getTrustName() { return trustName; }
    public void setTrustName(String trustName) { this.trustName = trustName; }
	public String getDonorContactNo() {
		return donorContactNo;
	}
	public void setDonorContactNo(String donorContactNo) {
		this.donorContactNo = donorContactNo;
	}
	public String getTrustContactNo() {
		return trustContactNo;
	}
	public void setTrustContactNo(String trustContactNo) {
		this.trustContactNo = trustContactNo;
	}
    
    
}








