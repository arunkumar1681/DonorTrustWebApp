package com.donorsignup.service;

import com.donorsignup.model.Trust;
import com.donorsignup.repository.TrustRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class TrustService {

	@Autowired
	private TrustRepository trustRepository;

	private static final String BASE_URL = "http://localhost:9090/api/trusts/uploads/";

	public boolean registerTrust(String trustName, String trustId, String email, String password, String contactNo, String fileName) {
	    if (trustRepository.findByEmail(email).isPresent()) {
	        return false;
	    }

	    Trust trust = new Trust();
	    trust.setTrustName(trustName);
	    trust.setTrustId(trustId);
	    trust.setEmail(email);
	    trust.setPassword(password);
	    trust.setContactNo(contactNo);
	    trust.setLicenseFilePath(BASE_URL + fileName);

	    trustRepository.save(trust);
	    return true;
	}


    public Optional<Trust> authenticateTrust(String email, String rawPassword) {
        return trustRepository.findByEmail(email)
                .filter(trust -> rawPassword.equals(trust.getPassword()));
    }

    public Optional<Trust> getTrustById(Long id) {
        return trustRepository.findById(id);
    }

    public boolean updateTrustDetails(Long id, Trust updatedTrust) {
        Optional<Trust> trustOptional = trustRepository.findById(id);
        if (trustOptional.isPresent()) {
            Trust trust = trustOptional.get();
            trust.setTrustName(updatedTrust.getTrustName());
            trust.setEmail(updatedTrust.getEmail());
            trust.setContactNo(updatedTrust.getContactNo());
            trust.setLocation(updatedTrust.getLocation());
            trustRepository.save(trust);
            return true;
        }
        return false;
    }
}




