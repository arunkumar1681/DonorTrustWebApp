package com.donorsignup.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.donorsignup.service.PhotoService;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@RestController
@RequestMapping("/api/photos")
@CrossOrigin(origins = "*")
public class PhotoController {

    @Autowired
    private PhotoService photoService;

    private static final String UPLOAD_DIR = "C:/uploads/";

    @PostMapping("/upload/{donorId}")
    public ResponseEntity<?> uploadPhoto(@PathVariable Long donorId, @RequestParam("file") MultipartFile file) {
        try {
            String fileName = photoService.savePhoto(donorId, file);
            if (fileName == null) {
                return ResponseEntity.status(400).body(Map.of("error", "Invalid donor ID"));
            }

            String photoUrl =  fileName;
            return ResponseEntity.ok(Map.of("message", "Photo uploaded successfully", "photoUrl", photoUrl));

        } catch (IOException e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to upload photo"));
        }
    }

    @GetMapping("/get/{donorId}")
    public ResponseEntity<Map<String, String>> getPhoto(@PathVariable Long donorId) {
        String fileName = photoService.getPhotoUrl(donorId);
        if (fileName != null) {
            String imageUrl = "http://localhost:9090/api/photos/uploads/" + fileName;
            return ResponseEntity.ok(Map.of("imageUrl", imageUrl));
        } else {
            return ResponseEntity.status(404).body(Map.of("error", "Photo not found"));
        }
    }


    @GetMapping("/uploads/{fileName}")
    public ResponseEntity<Resource> getUploadedPhoto(@PathVariable String fileName) {
        try {
            Path filePath = Paths.get(UPLOAD_DIR).resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_TYPE, "image/jpeg")
                        .body(resource);
            } else {
                return ResponseEntity.status(404).body(null);
            }

        } catch (MalformedURLException e) {
            return ResponseEntity.status(500).body(null);
        }
    }

}



