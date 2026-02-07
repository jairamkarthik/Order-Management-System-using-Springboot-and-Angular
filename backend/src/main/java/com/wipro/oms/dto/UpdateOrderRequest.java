package com.wipro.oms.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class UpdateOrderRequest {

    @Valid
    @NotNull
    private List<PlaceOrderRequest.Item> items;

    @Valid
    @NotNull
    private AddressDto address;

    public List<PlaceOrderRequest.Item> getItems() { return items; }
    public void setItems(List<PlaceOrderRequest.Item> items) { this.items = items; }

    public AddressDto getAddress() { return address; }
    public void setAddress(AddressDto address) { this.address = address; }
}
