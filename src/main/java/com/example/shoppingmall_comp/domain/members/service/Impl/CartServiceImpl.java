package com.example.shoppingmall_comp.domain.members.service.Impl;

import com.example.shoppingmall_comp.domain.items.entity.Item;
import com.example.shoppingmall_comp.domain.items.entity.SoldOutState;
import com.example.shoppingmall_comp.domain.items.repository.ItemRepository;
import com.example.shoppingmall_comp.domain.members.dto.CartRequest;
import com.example.shoppingmall_comp.domain.members.dto.CartResponse;
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
import java.util.stream.Collectors;

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
    public CartResponse create(CartRequest cartRequest, User user) {
        Member member = getMember(user);

        Item item = existItemCheck(cartRequest.itemId());

        //장바구니 넣으려는 상품 수량 > 실제 상품 재고
        int cartCount = cartRequest.count();
        if(cartCount > item.getCount()) {
            throw new BusinessException(NOT_ENOUGH_STOCK);
        }

        //품절 상태인지 확인
        if(item.getSoldOutState() == SoldOutState.SOLD_OUT) {
            throw new BusinessException(SOLD_OUT_STATE_ITEM, "상품이 품절되었습니다.");
        }

        //장바구니에 이미 존재하는 상품이면
        Cart existingCart = cartRepository.findByItemAndMember(item, member);
        if(existingCart != null) {
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
                .soldOutState(cartRequest.itemSoldOutState())
                .optionValues(options)
                .build();

        cartRepository.save(cart);

        return getCartResponse(cart);
    }

    //장바구니 수정
    @Override
    @Transactional
    public CartResponse update(CartRequest cartRequest, User user) {
        Member member = getMember(user);
        Cart cart = existMemberCartCheck(cartRequest.cartId(), member);

        cart.updateCart(cartRequest.count());
        cartRepository.save(cart);

        // 장바구니 수정은 수량만 수정이 가능해서 사실상 count만 바뀐걸 보여줘도 되지 않나? 이럴경우 Response 새로 파는지
        return getCartResponse(cart);
    }

    //장바구니 전체 조회
    @Override
    @Transactional(readOnly = true)
    public Page<CartResponse> getAll(Pageable pageable, User user) {
        Member member = getMember(user);
        //회원에 해당하는 전체 장바구니
        Page<Cart> cartList = cartRepository.findAllByMember(member, pageable);

        return cartList.map(this::getCartResponse);
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
                cart.getItem().getSoldOutState(),
                cart.getItem().getItemOption().getOptionValues().stream()
                        .map(option -> new CartResponse.Option(option.key(), option.value()))
                        .toList()
        );
    }
}
