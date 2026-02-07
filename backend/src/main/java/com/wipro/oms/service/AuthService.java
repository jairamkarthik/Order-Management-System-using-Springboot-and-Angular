package com.wipro.oms.service;

import com.wipro.oms.dto.AuthResponse;
import com.wipro.oms.dto.LoginRequest;
import com.wipro.oms.dto.RegisterRequest;
import com.wipro.oms.entity.AppUser;
import com.wipro.oms.entity.Role;
import com.wipro.oms.exception.BadRequestException;
import com.wipro.oms.repository.AppUserRepository;
import com.wipro.oms.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AppUserRepository userRepo;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;

    public AuthService(AppUserRepository userRepo,
                       PasswordEncoder encoder,
                       JwtService jwtService) {
        this.userRepo = userRepo;
        this.encoder = encoder;
        this.jwtService = jwtService;
    }


    public AuthResponse login(LoginRequest req) {
        AppUser user = userRepo.findByUsername(req.getUsername())
                .orElseThrow(() -> new BadRequestException("Invalid username or password"));

        if (!encoder.matches(req.getPassword(), user.getPasswordHash())) {
            throw new BadRequestException("Invalid username or password");
        }

        String token = jwtService.generateToken(user.getUsername(), user.getRole());
        return new AuthResponse(token, user.getRole().name());
    }
    public AuthResponse register(RegisterRequest req) {
        if (userRepo.existsByUsername(req.getUsername())) {
            throw new BadRequestException("Username already exists");
        }

        AppUser u = new AppUser();
        u.setUsername(req.getUsername());
        u.setPasswordHash(encoder.encode(req.getPassword()));
        u.setRole(Role.USER);

        userRepo.save(u);

        String token = jwtService.generateToken(u.getUsername(), u.getRole());
        return new AuthResponse(token, u.getRole().name());
    }
}