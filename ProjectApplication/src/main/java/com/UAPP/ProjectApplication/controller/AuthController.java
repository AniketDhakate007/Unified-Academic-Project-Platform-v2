package com.UAPP.ProjectApplication.controller;

import com.UAPP.ProjectApplication.model.User;
import com.UAPP.ProjectApplication.repository.UserRepository;
import com.UAPP.ProjectApplication.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.PostConstruct;
import java.util.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Autowired
    public AuthController(UserRepository userRepo, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @PostConstruct
    public void ensureAdminExists() {
        String adminUser = System.getProperty("ADMIN_USERNAME", System.getenv("ADMIN_USERNAME"));
        String adminPass = System.getProperty("ADMIN_PASSWORD", System.getenv("ADMIN_PASSWORD"));
        if (adminUser == null) adminUser = "admin";
        if (adminPass == null) adminPass = "password";

        if (!userRepo.existsByUsername(adminUser)) {
            User admin = new User(adminUser, passwordEncoder.encode(adminPass), Set.of("ROLE_ADMIN"));
            userRepo.save(admin);
            System.out.println("Created admin user: " + adminUser);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String,String> body) {
        String username = body.get("username");
        String password = body.get("password");
        if (username == null || password == null) {
            return ResponseEntity.badRequest().body(Map.of("message","username & password required"));
        }
        if (userRepo.existsByUsername(username)) {
            return ResponseEntity.badRequest().body(Map.of("message","Username exists"));
        }
        User u = new User(username, passwordEncoder.encode(password), Set.of("ROLE_STUDENT"));
        userRepo.save(u);
        return ResponseEntity.ok(Map.of("message","registered"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String,String> body) {
        String username = body.get("username");
        String password = body.get("password");
        if (username == null || password == null) {
            return ResponseEntity.badRequest().body(Map.of("message","username & password required"));
        }
        Optional<User> opt = userRepo.findByUsername(username);
        if (opt.isEmpty()) return ResponseEntity.status(401).body(Map.of("message","Invalid credentials"));
        User user = opt.get();
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            return ResponseEntity.status(401).body(Map.of("message","Invalid credentials"));
        }
        List<String> roles = new ArrayList<>(user.getRoles());
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), roles);
        return ResponseEntity.ok(Map.of("token", token, "roles", roles));
    }
}