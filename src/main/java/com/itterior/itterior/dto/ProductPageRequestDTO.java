package com.itterior.itterior.dto;

import com.itterior.itterior.domain.page.PageRequestDTO;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
public class ProductPageRequestDTO  extends PageRequestDTO{
    private Long category;
    private String sortBy;
    private String order;
}
