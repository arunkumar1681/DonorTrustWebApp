package com.donorsignup.Controller;

import com.donorsignup.model.Trust;
import com.donorsignup.repository.TrustRepository;
import com.donorsignup.service.TrustService;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/trusts")
@CrossOrigin(origins = "*")
public class TrustController {

    @Autowired
    private TrustService trustService;
    
    @Autowired
    private TrustRepository trustRepository;


    private static final String UPLOAD_DIR = "C:/uploads/";

    @PostMapping("/signup")
    public ResponseEntity<?> registerTrust(@RequestParam("trustName") String trustName,
                                           @RequestParam("trustId") String trustId,
                                           @RequestParam("email") String email,
                                           @RequestParam("password") String password,
                                           @RequestParam("contactNo") String contactNo,
                                           @RequestParam("licenseFile") MultipartFile file) {
        if (trustName.isBlank() || trustId.isBlank() || email.isBlank() || password.isBlank() || contactNo.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "All fields are required!"));
        }

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "License file is required!"));
        }

        try {
            // Save file
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists()) uploadDir.mkdirs();
            File destFile = new File(uploadDir, fileName);
            file.transferTo(destFile);

            boolean registered = trustService.registerTrust(trustName, trustId, email, password, contactNo, fileName);
            if (registered) {
                return ResponseEntity.ok(Map.of("message", "Trust registered successfully!"));
            } else {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "Trust already exists!"));
            }
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "File upload failed: " + e.getMessage()));
        }
    }


    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> loginTrust(@RequestParam("email") String email,
                                                          @RequestParam("password") String password) {
        Optional<Trust> trust = trustService.authenticateTrust(email, password);

        if (trust.isPresent()) {
            Trust trustObj = trust.get();

            String token = "dummy-jwt-token";

            Map<String, Object> response = Map.of(
                    "message", "Login successful!",
                    "trustId", trustObj.getTrustId(),
                    "id", trustObj.getId(),
                    "token", token
            );
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid email or password!"));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getTrustById(@PathVariable Long id) {
        Optional<Trust> trust = trustService.getTrustById(id);
        return trust.<ResponseEntity<Object>>map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.status(404).body(Map.of("error", "Trust not found!")));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTrustDetails(@PathVariable Long id, @RequestBody Trust updatedTrust) {
        Optional<Trust> trustOptional = trustRepository.findById(id);

        if (trustOptional.isPresent()) {
            Trust trust = trustOptional.get();
            trust.setTrustName(updatedTrust.getTrustName());
            trust.setEmail(updatedTrust.getEmail());
            trust.setContactNo(updatedTrust.getContactNo());
            trust.setLocation(updatedTrust.getLocation());

            Trust savedTrust = trustRepository.save(trust);
            return ResponseEntity.ok(savedTrust);
        } else {
            return ResponseEntity.status(404).body(Map.of("error", "Trust not found!"));
        }
    }



}



