package com.donorsignup.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.donorsignup.model.Trust;
import com.donorsignup.repository.TrustRepository;
import com.donorsignup.service.TrustPhotoService;

import org.springframework.http.MediaType;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/trust/photos")
public class TrustPhotoController {

    private static final String UPLOAD_DIR = "C:/uploads/";

    @Autowired
    private TrustPhotoService trustPhotoService;
    
    @Autowired
    private TrustRepository trustRepository;

    @PostMapping("/upload/{id}")
    public ResponseEntity<?> uploadPhoto(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        try {
            Optional<Trust> trustOptional = trustRepository.findById(id);
            if (trustOptional.isEmpty()) {
                return ResponseEntity.status(400).body(Map.of("error", "Invalid trust ID"));
            }

            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get("uploads").resolve(fileName);
            Files.write(filePath, file.getBytes());

            String photoUrl = fileName;

            Trust trust = trustOptional.get();
            trust.setProfilePictureUrl(photoUrl);
            trustRepository.save(trust);

            return ResponseEntity.ok(Map.of("message", "Photo uploaded successfully", "photoUrl", photoUrl));

        } catch (IOException e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to upload photo"));
        }
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<Map<String, String>> getPhoto(@PathVariable Long id) {
        String fileName = trustPhotoService.getPhotoUrl(id);
        if (fileName != null) {
            String imageUrl = "http://localhost:9090/api/trust/photos/uploads/" + fileName;
            return ResponseEntity.ok(Map.of("imageUrl", imageUrl));
        } else {
            return ResponseEntity.status(404).body(Map.of("error", "Photo not found"));
        }
    }

    @GetMapping("/uploads/{fileName}")
    public ResponseEntity<Resource> getUploadedPhoto(@PathVariable String fileName) {
        try {
            Path filePath = Paths.get("uploads").resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(resource);
        } catch (MalformedURLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}


