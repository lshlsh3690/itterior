package com.itterior.itterior.entity;

import com.itterior.itterior.domain.UserRole;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;


@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString(exclude = {"userRoleList", "products"})
@Table(name = "tbl_user")
@EntityListeners(AuditingEntityListener.class)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;
    private String pw;
    @Column(name = "user_userName")
    private String userName;
    private String email;
    private String nickname;
    private boolean social;
    private String profileImage;
    @ElementCollection
    @Builder.Default
    List<UserRole> roleList = new ArrayList<>();
    @OneToMany(mappedBy = "seller", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Product> products = new ArrayList<>();

//    @OneToMany(mappedBy = "user")
//    private List<Tokens> tokens;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;
    @Column(name = "updated_at", nullable = false)
//    @Temporal(TemporalType.TIMESTAMP)
    @LastModifiedDate
    private LocalDateTime updatedAt;

    public void addProduct(Product product) {
        product.setSeller(this);
        products.add(product);
    }

    public void removeProduct(Product product) {
        products.remove(product);
        product.setSeller(null);
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public void addRole(UserRole userRole) {
        this.roleList.add(userRole);
    }

    public void setProfileImageString(String fileName) {
        this.profileImage = fileName;
    }

}