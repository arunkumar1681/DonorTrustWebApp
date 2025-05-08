package com.donorsignup.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.donorsignup.model.Donor;
import java.util.Optional;

public interface DonorRepository extends JpaRepository<Donor, Long> {
	Optional<Donor> findByEmail(String email);

	Optional<Donor> findByResetToken(String resetToken);
}
