package com.example.springSequrityDemo2.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.springSequrityDemo2.entity.Admin;
import com.example.springSequrityDemo2.repo.AdminRepo;

import jakarta.transaction.Transactional;

import java.util.Optional;

@Service
public class AdminService {
  
    @Autowired
    private AdminRepo adminRepo;
  
    @Autowired
	private PasswordEncoder passwordEncoder;
  
    public Admin registerAdmin(Admin admin) {
        // Ensure role is ADMIN
        admin.setRole("ADMIN");
        // Encode password before saving
        admin.setPassword(getPasswordEncoder().encode(admin.getPassword()));
        return adminRepo.save(admin);
    }

    @Transactional
    public Admin updateAdmin(Admin updatedAdmin) {
        // Reload the current Admin entity from the database.
        Admin existingAdmin = adminRepo.findById(updatedAdmin.getId())
            .orElseThrow(() -> new RuntimeException("Admin not found"));
        
        // Update the fields as needed.
        existingAdmin.setUsername(updatedAdmin.getUsername());
        existingAdmin.setEmail(updatedAdmin.getEmail());
        // For password updates, encode if needed:
        if (updatedAdmin.getPassword() != null && !updatedAdmin.getPassword().isEmpty()) {
            existingAdmin.setPassword(getPasswordEncoder().encode(updatedAdmin.getPassword()));
        }
        
        // Save the managed entity.
        return adminRepo.save(existingAdmin);
    }

  
    public Optional<Admin> findByEmail(String email) {
        return adminRepo.findByEmail(email);
    }

	public PasswordEncoder getPasswordEncoder() {
		return passwordEncoder;
	}

	public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}
	public boolean verifyPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}

