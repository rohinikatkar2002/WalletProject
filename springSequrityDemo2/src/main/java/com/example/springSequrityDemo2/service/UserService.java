package com.example.springSequrityDemo2.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.springSequrityDemo2.entity.Users;
import com.example.springSequrityDemo2.repo.UserRepo;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@Service
public class UserService {
  
    @Autowired
    private UserRepo userRepo;
  
    @Autowired
    private PasswordEncoder passwordEncoder;
  
    public Users registerUser(Users user) {
        // Ensure role is USER
        user.setRole("USER");
        // Encode password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepo.save(user);
    }
  
    public Optional<Users> findByEmail(String email) {
        return userRepo.findByEmail(email);
    }
  
    public List<Users> getAllUsers() {
        return userRepo.findAll();
    }
  
    public Optional<Users> getUserById(Long id) {
        return userRepo.findById(id);
    }
  
    public Users updateUser(Long id, Users userDetails) {
        return userRepo.findById(id).map(user -> {
            user.setUsername(userDetails.getUsername());
            user.setEmail(userDetails.getEmail());
            // Optionally update password (encode it) if provided
            if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
                user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
            }
            return userRepo.save(user);
        }).orElseThrow(() -> new RuntimeException("User not found"));
    }
  
    public void deleteUser(Long id) {
        userRepo.deleteById(id);
    }
  
    public boolean verifyPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
