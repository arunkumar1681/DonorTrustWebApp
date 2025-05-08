package com.donorsignup.service;

import com.donorsignup.model.Trust;
import com.donorsignup.repository.TrustRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Optional; 

@Service
public class TrustPhotoService {

    private static final String UPLOAD_DIR = "C:/uploads/";

    @Autowired
    private TrustRepository trustRepository;

    public String savePhoto(Long id, MultipartFile file) throws IOException {
        Optional<Trust> trustOpt = trustRepository.findById(id);
        if (trustOpt.isEmpty()) return null;

        String fileName = id + "_" + file.getOriginalFilename();
        File destFile = new File(UPLOAD_DIR + fileName);
        file.transferTo(destFile);

        Trust trust = trustOpt.get();
        trust.setProfilePictureUrl(fileName); 
        trustRepository.save(trust);

        return fileName;
    }

    public String getPhotoUrl(Long id) {
        return trustRepository.findById(id)
                .map(Trust::getProfilePictureUrl)
                .orElse(null);
    }
}


