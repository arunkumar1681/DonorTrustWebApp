package com.donorsignup.repository;

import com.donorsignup.model.Trust;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TrustRepository extends JpaRepository<Trust, Long> {
	Optional<Trust> findByEmail(String email);
}
