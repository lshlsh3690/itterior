package com.itterior.itterior.dto;

import com.itterior.itterior.domain.page.PageRequestDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class ProductPageRequestDTOWithUsername  extends PageRequestDTO {
    private String username;
}