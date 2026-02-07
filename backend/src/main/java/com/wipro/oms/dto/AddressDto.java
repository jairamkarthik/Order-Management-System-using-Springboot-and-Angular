package com.wipro.oms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AddressDto {

    @NotBlank @Size(max = 120)
    private String fullName;

    @NotBlank @Size(max = 20)
    private String phone;

    @NotBlank @Size(max = 200)
    private String line1;

    @Size(max = 200)
    private String line2;

    @NotBlank @Size(max = 80)
    private String city;

    @NotBlank @Size(max = 80)
    private String state;

    @NotBlank @Size(max = 12)
    private String pincode;

    @NotBlank @Size(max = 80)
    private String country;

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getLine1() { return line1; }
    public void setLine1(String line1) { this.line1 = line1; }

    public String getLine2() { return line2; }
    public void setLine2(String line2) { this.line2 = line2; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getPincode() { return pincode; }
    public void setPincode(String pincode) { this.pincode = pincode; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
}
