package com.wipro.oms.controller;

import com.wipro.oms.dto.CreateUserRequest;
import com.wipro.oms.dto.UpdateUserRequest;
import com.wipro.oms.entity.AppUser;
import com.wipro.oms.service.UserAdminService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private final UserAdminService service;

    public AdminUserController(UserAdminService service) {
        this.service = service;
    }

    @PostMapping
    public AppUser create(@Valid @RequestBody CreateUserRequest req) {
        return service.create(req);
    }

    @GetMapping
    public List<AppUser> list(@RequestParam(required = false) String q) {
        return service.list(q);
    }

    @PatchMapping("/{id}")
    public AppUser update(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest req) {
        return service.update(id, req);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
