package com.example.springSequrityDemo2.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {

    // Use a strong, random key (this example key is long for demo purposes)
    private final String SECRET_KEY = "0cfc063356709a7d3d0f5fb7793bfc9a7ef7016c7372fd3152bd5cc2d5bc6981796b4411092bbe41bb3fba85282cdbdac99c73774dcf1c46a75390be8ca9d2a07a842bd54892c152183012445be12666fc7623f7ee2a39a67301293cb59a23c7fa173750558eaf5c3e09e3e92642ccd9be0c2bf729035a8e18a60678208c743165594598738cb65b5fbdaf78670ee4c45430b4b5fc7c6c5c891dc546e21de913746e5fe497391114d15475ba02525837c489f9908c39f32023e973d251b76d9e5ed2aa6199c7a9c1067ed7bcab0698fa2c1ff1b98c49e8b0946dc090edf72a6008c42f4b90230c60425d09dc722b8d9b777a093b823fa8435738e2e63462667f";
    
    private final long EXPIRATION_TIME = 1000 * 60 * 60; // 1 hour

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    // Extract username (in this example, the email is used)
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
  
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
  
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
  
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
  
    public Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
  
    // Generate token for a normal user or admin (token subject is the email)
    public  String generateToken(String email) {
        // Ensure the input email is valid!
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    
  
    // Validate token against a provided username
    public Boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }
}
