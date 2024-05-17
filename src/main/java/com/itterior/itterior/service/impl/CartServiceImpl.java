package com.itterior.itterior.service.impl;

import com.itterior.itterior.dto.CartItemDTO;
import com.itterior.itterior.dto.CartItemListDTO;
import com.itterior.itterior.dto.ItemDTO;
import com.itterior.itterior.entity.Cart;
import com.itterior.itterior.entity.CartItem;
import com.itterior.itterior.entity.Product;
import com.itterior.itterior.entity.User;
import com.itterior.itterior.repository.CartItemRepository;
import com.itterior.itterior.repository.CartRepository;
import com.itterior.itterior.repository.ProductRepository;
import com.itterior.itterior.repository.UserRepository;
import com.itterior.itterior.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Service
@Log4j2
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;

    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;

    private final ProductRepository productRepository;
    @Override
    public List<CartItemListDTO> addOrModify(CartItemDTO cartItemDTO) {

        String username = cartItemDTO.getUsername();

        Long pno = cartItemDTO.getPno();

        int qty = cartItemDTO.getQty();

        Long cino = cartItemDTO.getCino();

        log.info("======================================================");
        log.info(cartItemDTO.getCino() == null);

        if(cino != null) { //장바구니 아이템 번호가 있어서 수량만 변경하는 경우

            Optional<CartItem> cartItemResult = cartItemRepository.findById(cino);

            CartItem cartItem = cartItemResult.orElseThrow();

            cartItem.changeQty(qty);

            cartItemRepository.save(cartItem);

            return getCartItems(username);
        }

        //장바구니 아이템 번호 cino가 없는 경우

        //사용자의 카트
        Cart cart = getCart(username);

        CartItem cartItem = null;

        //이미 동일한 상품이 담긴적이 있을 수 있으므로
        cartItem = cartItemRepository.getItemOfPno(username, pno);

        if(cartItem == null){
            Product product = Product.builder().pno(pno).build();
            cartItem = CartItem.builder().product(product).cart(cart).qty(qty).build();

        }else {
            cartItem.changeQty(qty);
        }

        //상품 아이템 저장
        cartItemRepository.save(cartItem);

        return getCartItems(username);
    }


    //사용자의 장바구니가 없었다면 새로운 장바구니를 생성하고 반환
    private Cart getCart(String userName ){

        Cart cart = null;

        Optional<Cart> result = cartRepository.getCartOfMember(userName);

        if(result.isEmpty()) {

            log.info("Cart of the member is not exist!!");

            User user = userRepository.selectOneByUserName(userName).get();

            Cart tempCart = Cart.builder().owner(user).build();

            cart = cartRepository.save(tempCart);

        }else {
            cart = result.get();
        }

        return cart;

    }

    @Override
    public List<CartItemListDTO> getCartItems(String userName) {
        return cartItemRepository.getItemsOfCartDTOByUserName(userName);
    }

    @Override
    public ItemDTO addItem(ItemDTO itemDTO) {
        String username = itemDTO.getUsername();
        Long pno = itemDTO.getPno();
        int qty = itemDTO.getQty();

        Cart cart = getCart(username);

        CartItem cartItem = null;

        //이미 동일한 상품이 담긴적이 있을 수 있으므로
        cartItem = cartItemRepository.getItemOfPno(username, pno);

        if (cartItem == null) {
            Product product = null;
            Optional<Product> productById = productRepository.findById(pno);
            if (productById.isEmpty()) {
                //아이템이 존재하지 않으면 장바구니에 담을 수 없다
                log.info("아이템이 존재하지 않습니다.");
                return null;
            } else {
                product = productById.get();
            }
            cartItem = CartItem.builder().product(product).cart(cart).qty(qty).build();

        } else {
            cartItem.addQty(qty);
        }
        //상품 아이템 저장
        CartItem saveItem = cartItemRepository.save(cartItem);

        return ItemDTO.builder()
                .username(saveItem.getProduct().getSeller().getUserName())
                .pno(saveItem.getProduct().getPno())
                .qty(saveItem.getQty())
                .build();
    }

    @Override
    public List<CartItemListDTO> remove(Long cino) {

        Long cno  = cartItemRepository.getCartFromItem(cino);

        log.info("cart no: " + cno);

        cartItemRepository.deleteById(cino);

        return cartItemRepository.getItemsOfCartDTOByCart(cno);
    }
}
