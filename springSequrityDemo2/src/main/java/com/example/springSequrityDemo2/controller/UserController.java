package com.example.springSequrityDemo2.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.example.springSequrityDemo2.config.JwtUtil;
import com.example.springSequrityDemo2.entity.Admin;
import com.example.springSequrityDemo2.entity.Users;
import com.example.springSequrityDemo2.service.AdminService;
import com.example.springSequrityDemo2.service.UserService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {
  
    @Autowired
    private UserService userService;
    
    @Autowired
    private  AdminService adminService;

    @Autowired
    private JwtUtil util;
  
    // Public registration endpoint for users
    @PostMapping("/register")
    public ResponseEntity<Users> registerUser(@RequestBody Users user) {
        return ResponseEntity.ok(userService.registerUser(user));
    }
  
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Users user) {
        Optional<Users> existingUser = userService.findByEmail(user.getEmail());
        if (existingUser.isPresent() &&
                userService.verifyPassword(user.getPassword(), existingUser.get().getPassword())) {
            String token = util.generateToken(existingUser.get().getEmail());
            return ResponseEntity.ok("{\"token\":\"Bearer " + token + "\"}");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
    }

//  
    // For a user to update their own profile (or admin can update any profile)
    @PutMapping("/{id}")
    public ResponseEntity<Users> updateUser(@PathVariable Long id, @RequestBody Users userDetails) {
        String authEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER"));
  
        Optional<Users> currentUserOpt = userService.getUserById(id);
        if (currentUserOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Users currentUser = currentUserOpt.get();
  
        if (!isAdmin && !currentUser.getEmail().equals(authEmail)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
  
        return ResponseEntity.ok(userService.updateUser(id, userDetails));
    }
  
    // For a user to delete their own profile (or admin can delete any profile)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        String authEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
  
        Optional<Users> currentUserOpt = userService.getUserById(id);
        if (currentUserOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Users currentUser = currentUserOpt.get();
  
        if (!isAdmin && !currentUser.getEmail().equals(authEmail)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
  
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
  
    // Optionally, do not expose an endpoint to get all users for normal users.
    // For demonstration, we restrict get-all to admin only.
    @GetMapping("/")
    public ResponseEntity<List<Users>> getAllUsers() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(userService.getAllUsers());
    }
  
    // For a user to view their own profile (or admin can view any)
    @GetMapping("/{id}")
    public ResponseEntity<Users> getUserById(@PathVariable Long id) {
        String authEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
  
        Optional<Users> userOpt = userService.getUserById(id);
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Users user = userOpt.get();
        if (!isAdmin && !user.getEmail().equals(authEmail)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(user);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logoutUser(@RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            // No token provided, user is not logged in
            return ResponseEntity.status(401).body("User is not logged in.");
        }

        // Validate the token (optional, based on your JWT validation logic)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == "anonymousUser") {
            // Token is invalid or expired, user is not authenticated
            return ResponseEntity.status(401).body("User is not logged in or session expired.");
        }

        // If the user is authenticated, you can process logout (invalidate the session if needed)
        // In the case of JWT, the token itself is discarded client-side.
        return ResponseEntity.ok("User logged out successfully. Please remove the JWT token from client.");
    }
}

