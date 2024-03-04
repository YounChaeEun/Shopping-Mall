package com.example.shoppingmall_comp.domain.members.service.implement;

import com.example.shoppingmall_comp.domain.items.entity.Item;
import com.example.shoppingmall_comp.domain.items.entity.ItemState;
import com.example.shoppingmall_comp.domain.items.repository.ItemRepository;
import com.example.shoppingmall_comp.domain.members.dto.*;
import com.example.shoppingmall_comp.domain.members.entity.Cart;
import com.example.shoppingmall_comp.domain.members.entity.Member;
import com.example.shoppingmall_comp.domain.members.repository.CartRepository;
import com.example.shoppingmall_comp.domain.members.repository.MemberRepository;
import com.example.shoppingmall_comp.domain.members.service.CartService;
import com.example.shoppingmall_comp.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.example.shoppingmall_comp.global.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;

    //장바구니 생성
    @Override
    @Transactional
    public CartResponse create(CreateCartRequest cartRequest, User user) {
        Member member = getMember(user);

        Item item = existItemCheck(cartRequest.itemId());

        //장바구니 넣으려는 상품 수량 > 실제 상품 재고
        int cartItemCount = cartRequest.count();
        if (cartItemCount > item.getCount()) {
            throw new BusinessException(NOT_ENOUGH_STOCK);
        }

        //품절 상태인지 확인
        if (item.getItemState() == ItemState.SOLD_OUT) {
            throw new BusinessException(SOLD_OUT_STATE_ITEM, "상품이 품절되었습니다.");
        }
        //판매중단된 상태인지 확인
        if (item.getItemState() == ItemState.DISCONTINUED) {
            throw new BusinessException(DISCONTINUED_ITEM, "판매가 중단된 상품입니다.");
        }

        //장바구니에 이미 존재하는 상품이면
        Cart existingCart = cartRepository.findByItemAndMember(item, member);
        if (existingCart != null) {
            throw new BusinessException(CART_IN_ITEM_DUPLICATED);
        }

        // CartRequest.Option -> Cart.Option
        List<Cart.Option> options = cartRequest.optionValue().stream()
                .map(option -> new Cart.Option(option.key(), option.value()))
                .toList();

        // 엔티티는 빌더로
        Cart cart = Cart.builder()
                .count(cartRequest.count())
                .member(member)
                .item(item)
                .optionValues(options)
                .build();

        cartRepository.save(cart);

        return getCartResponse(cart);
    }

    //장바구니 수정
    @Override
    @Transactional
    public void update(Long cartId, UpdateCartRequest cartRequest, User user) {
        Member member = getMember(user);
        Cart cart = existMemberCartCheck(cartId, member);

        cart.updateCart(cartRequest.count());
        cartRepository.save(cart);
    }

    //장바구니 전체 조회
    @Override
    @Transactional(readOnly = true)
    public CartPageResponse getAll(Pageable pageable, User user) {
        Member member = getMember(user);
        //회원에 해당하는 전체 장바구니
        Page<Cart> cartList = cartRepository.findAllByMember(member, pageable);

        List<CartResponse> cartItems = cartList.getContent().stream()
                .map(cart -> new CartResponse(
                        cart.getCartId(),
                        cart.getCount(),
                        cart.getItem().getItemId(),
                        cart.getItem().getItemName(),
                        cart.getItem().getItemPrice(),
                        cart.getItemState(),
                        cart.getItem().getItemOption().getOptionValues().stream()
                                .map(option -> new CartResponse.Option(option.key(), option.value()))
                                .toList()
                ))
                .toList();

        return new CartPageResponse(
                cartList.getTotalPages(),
                (int) cartList.getTotalElements(),
                cartList.getNumber(),
                cartList.getSize(),
                cartItems
        );
    }

    //체크된 장바구니들 삭제
    @Override
    @Transactional
    public void deleteSelectedCarts(List<Long> cartIds, User user) {
        Member member = getMember(user);
        for (Long cartId : cartIds) {
            Cart cart = existMemberCartCheck(cartId, member);
            cartRepository.delete(cart);
        }
    }

    private Member getMember(User user) {
        return memberRepository.findByEmail(user.getUsername())
                .orElseThrow(() -> new BusinessException(NOT_FOUND_MEMBER));
    }

    //상품 존재 여부 확인
    private Item existItemCheck(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new BusinessException(NOT_FOUND_ITEM, "상품이 존재하지 않습니다."));
    }

    //회원의 장바구니 존재 여부 확인
    private Cart existMemberCartCheck(Long cartId, Member member) {
        return cartRepository.findByCartIdAndMember(cartId, member)
                .orElseThrow(() -> new BusinessException(NOT_FOUND_CART));
    }

    private CartResponse getCartResponse(Cart cart) {
        return new CartResponse(
                cart.getCartId(),
                cart.getCount(),
                cart.getItem().getItemId(),
                cart.getItem().getItemName(),
                cart.getItem().getItemPrice(),
                cart.getItem().getItemState(),
                cart.getItem().getItemOption().getOptionValues().stream()
                        .map(option -> new CartResponse.Option(option.key(), option.value()))
                        .toList()
        );
    }
}
