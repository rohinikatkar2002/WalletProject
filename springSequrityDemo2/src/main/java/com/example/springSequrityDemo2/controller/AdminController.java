package com.example.springSequrityDemo2.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.example.springSequrityDemo2.config.JwtUtil;
import com.example.springSequrityDemo2.entity.Admin;
import com.example.springSequrityDemo2.service.AdminService;

import java.util.Optional;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
  
    @Autowired
    private AdminService adminService;
    @Autowired
    private JwtUtil util;
  
    // Public registration endpoint for admins
    @PostMapping("/register")
    public ResponseEntity<Admin> registerAdmin(@RequestBody Admin admin) {
        // Force role to ADMIN
        admin.setRole("ADMIN");
        return ResponseEntity.ok(adminService.registerAdmin(admin));
    }
  
    // Public login endpoint for admins
    @PostMapping("/login")
    public ResponseEntity<?> loginAdmin(@RequestBody Admin admin) {
        Optional<Admin> existingAdmin = adminService.findByEmail(admin.getEmail());
        if (existingAdmin.isEmpty()) {
            // Return unauthorized if no admin is found
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials: admin not found");
        }
        // Verify password only if admin exists
        if (!adminService.verifyPassword(admin.getPassword(), existingAdmin.get().getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials: wrong password");
        }
        String token = util.generateToken(existingAdmin.get().getEmail());
        return ResponseEntity.ok("{\"token\":\"Bearer " + token + "\"}");
    }
    @PostMapping("/logout")
    public ResponseEntity<String> logoutAdmin(@RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            // No token provided, admin is not logged in
            return ResponseEntity.status(401).body("Admin is not logged in.");
        }

        // Validate the token (optional, based on your JWT validation logic)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == "anonymousUser") {
            // Token is invalid or expired, admin is not authenticated
            return ResponseEntity.status(401).body("Admin is not logged in or session expired.");
        }

        // If the admin is authenticated, you can process logout (invalidate the session if needed)
        // In the case of JWT, the token itself is discarded client-side.
        return ResponseEntity.ok("Admin logged out successfully. Please remove the JWT token from client.");
    }
    // Admin-specific endpoints (for example, view all users, delete users) are implemented in AdminController or via the /api/admin/** URL.
    // Here, you might include additional endpoints exclusive to admin functions.
}
