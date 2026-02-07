package com.wipro.oms.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.List;

import com.wipro.oms.dto.AddressDto;

public class PlaceOrderRequest {

    @Valid
    @NotNull
    private List<Item> items;

    @Valid
    @NotNull
    private AddressDto address;

    public List<Item> getItems() { return items; }
    public void setItems(List<Item> items) { this.items = items; }

    public AddressDto getAddress() { return address; }
    public void setAddress(AddressDto address) { this.address = address; }

    public static class Item {
        @NotNull
        private Long productId;

        @NotNull
        @Min(1)
        private Integer qty;

        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }

        public Integer getQty() { return qty; }
        public void setQty(Integer qty) { this.qty = qty; }
    }
}
