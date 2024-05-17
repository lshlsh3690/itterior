package com.itterior.itterior.repository;

import com.itterior.itterior.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long>{
    @Query("select cart from Cart cart where cart.owner.userName = :userName")
    public Optional<Cart> getCartOfMember(@Param("userName") String userName);

    public Optional<Cart> findByOwnerUserName(String userName);

}