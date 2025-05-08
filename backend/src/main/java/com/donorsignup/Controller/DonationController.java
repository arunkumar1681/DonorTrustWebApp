package com.donorsignup.Controller;

import com.donorsignup.model.Donation;
import com.donorsignup.model.Donor;
import com.donorsignup.service.DonationService;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.util.StringUtils;
import java.nio.file.*;

@RestController
@RequestMapping("/api/donations")
@CrossOrigin(origins = "*")
public class DonationController {

    private final DonationService donationService;


    private static final String UPLOAD_DIR = "uploads/";

    public DonationController(DonationService donationService) {
        this.donationService = donationService;
    }

    @PostMapping(value = "/add", consumes = "multipart/form-data")
    public ResponseEntity<?> addDonation(
            @RequestParam("donationType") String donationType,
            @RequestParam("description") String description,
            @RequestParam("quantity") String quantity,
            @RequestParam("location") String location,
            @RequestParam("photo") MultipartFile photo,
            @RequestParam("donorId") Long donorId) {

        try {
            if (photo.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Image file is required!");
            }

            String fileName = System.currentTimeMillis() + "_" + StringUtils.cleanPath(photo.getOriginalFilename());
            Path filePath = Paths.get(UPLOAD_DIR + fileName);
            Files.createDirectories(filePath.getParent());
            Files.write(filePath, photo.getBytes());

            String photoUrl = "http://localhost:9090/api/donations/uploads/" + fileName; 

            Optional<Donor> donorOpt = donationService.findDonorById(donorId);
            if (!donorOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Donor not found with ID: " + donorId);
            }

            Donor donor = donorOpt.get();
            Donation donation = new Donation();
            donation.setDonationType(donationType);
            donation.setDescription(description);
            donation.setQuantity(quantity);
            donation.setLocation(location);
            donation.setPhotoUrl(photoUrl);
            donation.setDonor(donor);

            Donation savedDonation = donationService.saveDonation(donation);
            
            return ResponseEntity.ok(savedDonation);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving donation: " + e.getMessage());
        }
    }

    @GetMapping("/uploads/{fileName}")
    public ResponseEntity<Resource> getImage(@PathVariable String fileName) {
        try {
            Path filePath = Paths.get(UPLOAD_DIR + fileName);
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_TYPE, Files.probeContentType(filePath))
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/get")
    public ResponseEntity<List<Donation>> getAllDonations() {
        List<Donation> donations = donationService.getAllDonations();
        return ResponseEntity.ok(donations);
    }
    
    @PutMapping("/accept/{donationId}/{trustId}")
    public ResponseEntity<?> acceptDonation(
            @PathVariable Long donationId,
            @PathVariable Long trustId) {
        try {
            Donation updatedDonation = donationService.acceptDonation(donationId, trustId);
            
            return ResponseEntity.ok(updatedDonation);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    
    @GetMapping("/history/{donorId}")
    public List<Donation> getDonationHistory(@PathVariable Long donorId) {
        return donationService.getDonationHistoryByDonorId(donorId);
    }
    
    @GetMapping("/trust-history/{trustId}")
    public List<Donation> getTrustHistory(@PathVariable Long trustId) {
        return donationService.getTrustHistory(trustId);
    }
}






