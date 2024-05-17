package com.itterior.itterior.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString(exclude = "owner")
@Table(
        name = "tbl_cart",
        indexes = { @Index(name="idx_cart_username", columnList = "user_owner") }
)
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cno;

    @OneToOne
    @JoinColumn(name="user_owner")
    private User owner;

}