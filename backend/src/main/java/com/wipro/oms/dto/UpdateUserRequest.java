package com.wipro.oms.dto;

import com.wipro.oms.entity.Role;
import jakarta.validation.constraints.Size;

public class UpdateUserRequest {

    private Role role;

    @Size(min = 6, max = 100)
    private String password;

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
