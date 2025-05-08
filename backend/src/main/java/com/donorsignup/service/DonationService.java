package com.donorsignup.service;

import com.donorsignup.model.Donation;
import com.donorsignup.model.Donor;
import com.donorsignup.model.Trust;
import com.donorsignup.repository.DonationRepository;
import com.donorsignup.repository.DonorRepository;
import com.donorsignup.repository.TrustRepository;
import org.springframework.stereotype.Service;
import com.donorsignup.service.TrustService;

import java.util.List;
import java.util.Optional;

@Service
public class DonationService {
    private final DonationRepository donationRepository;
    private final DonorRepository donorRepository;
    private final TrustRepository trustRepository;

    public DonationService(DonationRepository donationRepository, DonorRepository donorRepository, TrustRepository trustRepository) {
        this.donationRepository = donationRepository;
        this.donorRepository = donorRepository;
        this.trustRepository = trustRepository;
    }

    public List<Donation> getAllDonations() {
        return donationRepository.findAll();
    }

    public Optional<Donation> getDonationById(Long id) {
        return donationRepository.findById(id);
    }

    public Donation saveDonation(Donation donation) {
        if (donation.getDonor() == null || donation.getDonor().getId() == null) {
            throw new IllegalArgumentException("Donor ID is required");
        }

        Optional<Donor> donorOpt = donorRepository.findById(donation.getDonor().getId());
        if (!donorOpt.isPresent()) {
            throw new IllegalArgumentException("Donor not found with ID: " + donation.getDonor().getId());
        }

        Donor donor = donorOpt.get();
        donation.setDonor(donor);  
        donation.setDonorName(donor.getName());  
        donation.setDonorContactNo(donor.getContactNo()); 

        return donationRepository.save(donation);
    }


    public Optional<Donor> findDonorById(Long donorId) {
        return donorRepository.findById(donorId);
    }
    
    public Donation acceptDonation(Long donationId, Long trustId) {
        Optional<Donation> donationOpt = donationRepository.findById(donationId);
        if (!donationOpt.isPresent()) {
            throw new IllegalArgumentException("Donation not found with ID: " + donationId);
        }

        Donation donation = donationOpt.get();
        
        Optional<Trust> trustOpt = trustRepository.findById(trustId);
        if (!trustOpt.isPresent()) {
            throw new IllegalArgumentException("Trust not found with ID: " + trustId);
        }

        Trust trust = trustOpt.get();

        donation.setStatus(true); 
        donation.setTrustId(trust.getId());
        donation.setTrustName(trust.getTrustName());
        donation.setTrustContactNo(trust.getContactNo());

        return donationRepository.save(donation);
    }
    
    public List<Donation> getDonationHistoryByDonorId(Long donorId) {
        return donationRepository.findByDonorIdOrderByIdDesc(donorId);
    }
    
    public List<Donation> getTrustHistory(Long trustId) {
        return donationRepository.findByTrustIdAndStatus(trustId, true);
    }
    

}



