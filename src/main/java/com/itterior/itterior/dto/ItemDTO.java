package com.itterior.itterior.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ItemDTO {
    private String username;

    private Long pno;

    private int qty;
}
