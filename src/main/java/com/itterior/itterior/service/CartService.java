package com.itterior.itterior.service;

import com.itterior.itterior.dto.CartItemDTO;
import com.itterior.itterior.dto.CartItemListDTO;
import com.itterior.itterior.dto.ItemDTO;

import java.util.List;

public interface CartService {
    List<CartItemListDTO> remove(Long cino);

    List<CartItemListDTO> addOrModify(CartItemDTO itemDTO);

    List<CartItemListDTO> getCartItems(String userName);

    ItemDTO addItem(ItemDTO itemDTO);
}
