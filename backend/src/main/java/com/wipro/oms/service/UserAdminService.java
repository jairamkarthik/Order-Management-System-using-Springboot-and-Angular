package com.wipro.oms.service;

import com.wipro.oms.dto.CreateUserRequest;
import com.wipro.oms.dto.UpdateUserRequest;
import com.wipro.oms.entity.AppUser;
import com.wipro.oms.exception.BadRequestException;
import com.wipro.oms.exception.NotFoundException;
import com.wipro.oms.repository.AppUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserAdminService {

    private final AppUserRepository repo;
    private final PasswordEncoder encoder;

    public UserAdminService(AppUserRepository repo, PasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
    }

    public AppUser create(CreateUserRequest req) {
        if (repo.existsByUsername(req.getUsername())) {
            throw new BadRequestException("Username already exists");
        }
        AppUser u = new AppUser();
        u.setUsername(req.getUsername());
        u.setPasswordHash(encoder.encode(req.getPassword()));
        u.setRole(req.getRole());
        return repo.save(u);
    }

    public List<AppUser> list(String q) {
        if (q == null || q.isBlank()) return repo.findAll();
        String keyword = q.trim().toLowerCase();
        return repo.findAll().stream()
                .filter(u -> u.getUsername() != null && u.getUsername().toLowerCase().contains(keyword))
                .toList();
    }

    public AppUser update(Long id, UpdateUserRequest req) {
        AppUser u = repo.findById(id).orElseThrow(() -> new NotFoundException("User not found: " + id));
        if ("admin".equalsIgnoreCase(u.getUsername())) {
            throw new BadRequestException("Cannot modify default admin");
        }

        if (req.getRole() != null) {
            u.setRole(req.getRole());
        }
        if (req.getPassword() != null && !req.getPassword().isBlank()) {
            u.setPasswordHash(encoder.encode(req.getPassword()));
        }
        return repo.save(u);
    }

    public void delete(Long id) {
        AppUser u = repo.findById(id).orElseThrow(() -> new NotFoundException("User not found: " + id));
        if ("admin".equalsIgnoreCase(u.getUsername())) {
            throw new BadRequestException("Cannot delete default admin");
        }
        repo.delete(u);
    }
}
