package com.itterior.itterior.controller;

import com.itterior.itterior.dto.CartItemDTO;
import com.itterior.itterior.dto.CartItemListDTO;
import com.itterior.itterior.dto.ItemDTO;
import com.itterior.itterior.service.CartService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/api/cart")
@Tag(name = "Cart", description = "Cart API")
public class CartController {

    private final CartService cartService;

    @PreAuthorize("#itemDTO.username == authentication.username")
    @PostMapping("/change")
    public List<CartItemListDTO> changeCart(@RequestBody CartItemDTO itemDTO){

        log.info(itemDTO);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            log.info("Current user: {}", username);
        }

        if(itemDTO.getQty() <= 0) {
            log.info("삭제");
            return cartService.remove(itemDTO.getCino());
        }
        return cartService.addOrModify(itemDTO);
    }


    @PreAuthorize("hasAnyRole('ROLE_USER')")
    @GetMapping("/items")
    public List<CartItemListDTO> getCartItems(Principal principal) {

        log.info("getCartItems() " + principal);

        String userName = principal.getName();

        List<CartItemListDTO> cartItems = cartService.getCartItems(userName);

        log.info(cartItems);

        return cartItems;
    }

    @PreAuthorize("hasAnyRole('ROLE_USER')")
    @DeleteMapping("/{cino}")
    public List<CartItemListDTO> removeFromCart( @PathVariable("cino") Long cino){

        log.info("cart item no: " + cino);

        return cartService.remove(cino);
    }


    @PostMapping("/add")
    @PreAuthorize("hasAnyRole('ROLE_USER')")
    public ItemDTO addToCart(@RequestBody ItemDTO itemDTO){
        log.info(itemDTO);


        return cartService.addItem(itemDTO);
    }

}