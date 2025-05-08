package com.donorsignup.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.donorsignup.model.Donor;
import com.donorsignup.service.DonorService;

import jakarta.servlet.http.HttpSession;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/donors")
@CrossOrigin(origins = "*") 

public class DonorController {

	@Autowired
	private DonorService donorService;
	

	@PostMapping("/register")
	public ResponseEntity<String> registerDonor(@RequestParam("name") String name, @RequestParam("email") String email,
			@RequestParam("password") String password, @RequestParam("contactNo") String contactNo,
			@RequestParam("govId") MultipartFile govIdFile) {

		try {
			String uploadDir = "C:/uploads/";
			File directory = new File(uploadDir);
			if (!directory.exists()) {
				directory.mkdirs(); 
			}

			String fileName = System.currentTimeMillis() + "_" + govIdFile.getOriginalFilename();
			File file = new File(uploadDir + fileName);
			govIdFile.transferTo(file);

			Donor donor = new Donor();
			donor.setName(name);
			donor.setEmail(email);
			donor.setPassword(password);
			donor.setContactNo(contactNo);
			donor.setGovIdPath(file.getAbsolutePath());

			donorService.saveDonor(donor);
			return ResponseEntity.ok("Donor registered successfully!");
		} catch (IOException e) {
			return ResponseEntity.status(500).body("Error saving file");
		}
	}

	@PostMapping("/login")
	public ResponseEntity<Map<String, Object>> loginDonor(@RequestBody Donor donor, HttpSession session) {
	    if (donor.getEmail() == null || donor.getPassword() == null) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                .body(Map.of("error", "Email and password are required"));
	    }

	    Donor existingDonor = donorService.findByEmail(donor.getEmail());
	    if (existingDonor == null) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
	                .body(Map.of("error", "Invalid email or password"));
	    }

	    boolean isValid = donorService.validateLogin(donor.getEmail(), donor.getPassword());

	    if (isValid) {
	        session.setAttribute("donorId", existingDonor.getId());

	        Map<String, Object> response = new HashMap<>();
	        response.put("message", "Login Successful");
	        response.put("donorId", existingDonor.getId());
	        response.put("token", "dummy-token-123");

	        return ResponseEntity.ok(response);
	    } else {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
	                .body(Map.of("error", "Invalid email or password"));
	    }
	}

	@GetMapping("/profile/{id}")
	public ResponseEntity<?> getDonorProfile(@PathVariable Long id) {
	    Donor donor = donorService.getDonorById(id);

	    if (donor == null) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                .body(Map.of("error", "Donor not found."));
	    }

	    Map<String, Object> response = new HashMap<>();
	    response.put("name", donor.getName());
	    response.put("email", donor.getEmail());
	    response.put("contactNo", donor.getContactNo());
	    response.put("location", donor.getLocation());
	    response.put("profilePicture", donor.getProfilePicture());

	    return ResponseEntity.ok(response);
	}

	@PostMapping("/update/{id}")
	public ResponseEntity<?> updateDonor(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
	    Donor donor = donorService.getDonorById(id);

	    if (donor == null) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                .body(Map.of("error", "Donor not found."));
	    }

	    updates.forEach((key, value) -> {
	        switch (key) {
	            case "name": donor.setName((String) value); break;
	            case "email": donor.setEmail((String) value); break;
	            case "contactNo": donor.setContactNo((String) value); break;
	            case "location": donor.setLocation((String) value); break;
	        }
	    });
	    donorService.saveDonor(donor);
	    return ResponseEntity.ok(donor);
	}
	

}
