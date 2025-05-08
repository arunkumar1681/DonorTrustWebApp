package com.donorsignup.Controller;

import com.donorsignup.service.TwilioService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/api/otp")
@CrossOrigin(origins = "*") 
public class OtpController {

    private final TwilioService twilioService;
    private final Map<String, String> otpStore = new HashMap<>();

    public OtpController(TwilioService twilioService) {
        this.twilioService = twilioService;
    }

    @PostMapping("/send")
    public Map<String, String> sendOtp(@RequestBody Map<String, String> request) {
        String phoneNumber = request.get("phoneNumber");
        String otp = String.valueOf(new Random().nextInt(900000) + 100000); // Generate 6-digit OTP

        otpStore.put(phoneNumber, otp);
        twilioService.sendOtp(phoneNumber, otp);

        Map<String, String> response = new HashMap<>();
        response.put("message", "OTP sent successfully!");
        return response;
    }

    @PostMapping("/verify")
    public Map<String, String> verifyOtp(@RequestBody Map<String, String> request) {
        String phoneNumber = request.get("phoneNumber");
        String otp = request.get("otp");

        Map<String, String> response = new HashMap<>();
        if (otpStore.containsKey(phoneNumber) && otpStore.get(phoneNumber).equals(otp)) {
            response.put("message", "OTP verified successfully!");
            otpStore.remove(phoneNumber);
        } else {
            response.put("message", "Invalid OTP!");
        }

        return response;
    }
}

