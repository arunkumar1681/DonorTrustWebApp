package com.donorsignup.service;

import com.donorsignup.model.Donor;
import com.donorsignup.repository.DonorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

@Service
public class PhotoService {

    private static final String UPLOAD_DIR = "C:/uploads/";

    @Autowired
    private DonorRepository donorRepository;

    public String savePhoto(Long donorId, MultipartFile file) throws IOException {
        Optional<Donor> donorOpt = donorRepository.findById(donorId);
        if (donorOpt.isEmpty()) return null;

        String fileName = donorId + "_" + file.getOriginalFilename();
        File destFile = new File(UPLOAD_DIR + fileName);
        file.transferTo(destFile);

        Donor donor = donorOpt.get();
        donor.setProfilePicture("http://localhost:9090/api/photos/uploads/" + fileName);
        donorRepository.save(donor);

        return fileName;
    }


    public String getPhotoUrl(Long donorId) {
        return donorRepository.findById(donorId)
                .map(donor -> donor.getProfilePicture())
                .orElse(null);
    }
}


