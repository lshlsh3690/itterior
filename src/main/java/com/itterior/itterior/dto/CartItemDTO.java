package com.itterior.itterior.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CartItemDTO {

    private String username;

    private Long pno;

    private int qty;

    public Long Cino;
}