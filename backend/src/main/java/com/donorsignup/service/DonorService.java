package com.donorsignup.service;

import com.donorsignup.model.Donor;
import com.donorsignup.repository.DonorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class DonorService {

	@Autowired
	private DonorRepository donorRepository;

	public Donor saveDonor(Donor donor) {
        return donorRepository.save(donor);
    }

    public Donor findByEmail(String email) {
        Optional<Donor> donorOpt = donorRepository.findByEmail(email);
        return donorOpt.orElse(null);
    }

    public boolean validateLogin(String email, String password) {
        Optional<Donor> donorOpt = donorRepository.findByEmail(email);
        if (donorOpt.isPresent()) {
            Donor donor = donorOpt.get();
            return donor.getPassword().equals(password); 
        }
        return false;
    }

    public Long getDonorIdByEmail(String email) {
        Optional<Donor> donor = donorRepository.findByEmail(email);
        return donor.map(Donor::getId).orElse(null);
    }

	
	public Donor getDonorById(Long id) {
        return donorRepository.findById(id).orElse(null);
    }

	public Donor updateDonor(Long id, Donor updatedDonor) {
	    Optional<Donor> existingDonorOpt = donorRepository.findById(id);
	    
	    if (existingDonorOpt.isPresent()) {
	        Donor existingDonor = existingDonorOpt.get();
	        
	        existingDonor.setName(updatedDonor.getName());
	        existingDonor.setEmail(updatedDonor.getEmail());
	        existingDonor.setContactNo(updatedDonor.getContactNo());
	        existingDonor.setLocation(updatedDonor.getLocation());
	        return donorRepository.save(existingDonor);
	    } else {
	        throw new RuntimeException("Donor not found!");
	    }
	}

}
