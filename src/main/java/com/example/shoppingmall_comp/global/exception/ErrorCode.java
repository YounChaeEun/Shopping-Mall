package com.example.shoppingmall_comp.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    /**
     * ********************************************** DOMAIN *************************************************
     */

    //회원
    NOT_FOUND_MEMBER(HttpStatus.NOT_FOUND, "존재하지 않는 회원입니다."),
    NOT_EQUAL_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호를 확인해주세요"),
    CHECK_LOGIN_ID_OR_PASSWORD(HttpStatus.NOT_FOUND, "아이디 또는 비밀번호를 확인해주세요."),
    DUPLICATED_LOGIN_ID(HttpStatus.BAD_REQUEST, "이미 존재하는 아이디 입니다."),
    DUPLICATED_EMAIL(HttpStatus.BAD_REQUEST, "이미 존재하는 이메일 입니다."),
    OTHER_LOGIN_TYPE(HttpStatus.BAD_REQUEST, "로그인 타입을 확인해주세요"),
    NOT_FOUND_AUTHORITY(HttpStatus.NOT_FOUND, "존재하지 않는 권한입니다."),
    FORBIDDEN_ERROR(HttpStatus.FORBIDDEN, "권한이 없습니다."),
    NOT_USER(HttpStatus.FORBIDDEN, "구매자가 아닙니다."),
    NOT_SELLER(HttpStatus.FORBIDDEN, "판매자가 아닙니다."),
    NOT_ADMIN(HttpStatus.FORBIDDEN, "관리자가 아닙니다."),

    EXPIRED_JWT_ERROR(HttpStatus.UNAUTHORIZED, "토큰 만료 기한이 지났습니다."),
    INVALID_JWT_ERROR(HttpStatus.FORBIDDEN, "토큰 유효성 검사가 실패했습니다."),
    USER_AUTH_ERROR(HttpStatus.UNAUTHORIZED, "토큰 검사시 사용자 인증에 실패했습니다."),
    TOKEN_CLAIM_EMPTY(HttpStatus.BAD_REQUEST,"JWT 클레임 문자열이 비어있습니다."),
    UNSUPPORTED_JWT_TOKEN(HttpStatus.BAD_REQUEST,"지원되지 않는 JWT 토큰입니다."),
    NOT_FOUND_REFRESH_TOKEN(HttpStatus.NOT_FOUND, "존재하지 않는 리프레시 토큰입니다."),


    // 상품
    NOT_FOUND_ITEM(HttpStatus.NOT_FOUND, "존재하지 않는 상품입니다."),
    DUPLICATE_ITEM(HttpStatus.BAD_REQUEST, "이미 존재하는 상품입니다."),
    NOT_BUY_ITEM(HttpStatus.BAD_REQUEST, "구매한 상품이 아닙니다."),
    NOT_SELLING_ITEM(HttpStatus.BAD_REQUEST, "판매하고 있는 상품이 아닙니다."),
    REQUIRED_IMAGE(HttpStatus.BAD_REQUEST, "이미지는 필수로 등록해야 합니다."),
    UPLOAD_ERROR_IMAGE(HttpStatus.BAD_REQUEST, "이미지 업로드 에러가 발생했습니다."),
    VALID_ERROR_IMAGE(HttpStatus.BAD_REQUEST, "이미지 형식을 확인해주세요."),
    SOLD_OUT_STATE_ITEM(HttpStatus.BAD_REQUEST, "상품이 품절되었습니다."),
    DISCONTINUED_ITEM(HttpStatus.BAD_REQUEST, "판매가 중단된 상품입니다."),

    // 옵션
    NOT_FOUND_OPTION(HttpStatus.NOT_FOUND, "존재하지 않는 옵션입니다."),

    // 장바구니
    NOT_FOUND_CART(HttpStatus.NOT_FOUND, "존재하지 않는 장바구니 입니다."),
    CART_NO_PRODUCTS(HttpStatus.NOT_FOUND, "장바구니에 상품이 존재하지 않습니다."),
    CART_IN_ITEM_DUPLICATED(HttpStatus.NOT_FOUND, "장바구니에 이미 담겨있는 상품입니다."),

    // 카테고리
    NOT_FOUND_CATEGORY(HttpStatus.NOT_FOUND, "존재하지 않는 카테고리입니다"),
    CATEGORY_NAME_DUPLICATED(HttpStatus.BAD_REQUEST, "중복된 카테고리 이름입니다."),
    CATEGORY_EXIST_GOODS(HttpStatus.BAD_REQUEST, "카테고리에 속한 상품이 존재합니다."),

    // 리뷰
    NOT_FOUND_REVIEW(HttpStatus.NOT_FOUND, "존재하는 리뷰가 없습니다."),
    NOT_MATCH_REVIEW(HttpStatus.FORBIDDEN, "해당 회원이 작성한 리뷰가 아닙니다."),
    CANT_WRITE_REVIEW(HttpStatus.FORBIDDEN, "주문한 상품에 대해서만 리뷰를 작성할 수 있습니다."),
    CANT_DELETE_REVIEW(HttpStatus.BAD_REQUEST, "삭제할 수 없는 리뷰입니다."),
    PASS_POSSIBLE_DATE(HttpStatus.INTERNAL_SERVER_ERROR, "리뷰 작성 가능한 기간이 지났습니다."),

    // 주문
    NOT_FOUND_ORDERS(HttpStatus.NOT_FOUND, "존재하는 주문이 없습니다."),
    NO_BUY_ORDER(HttpStatus.BAD_REQUEST, "구매한 상품이 아닙니다."),
    NOT_ENOUGH_STOCK(HttpStatus.BAD_REQUEST, "주문하려고 하는 상품 재고가 부족합니다."),
    NOT_FOUND_ORDER_ITEM(HttpStatus.NOT_FOUND, "존재하는 주문 상품이 없습니다."),

    // 결제
    NOT_FOUND_PAY(HttpStatus.NOT_FOUND, "존재하는 결제가 없습니다."),
    ALREADY_CANCEL_PAY(HttpStatus.BAD_REQUEST, "이미 취소된 결제입니다."),
    CAN_NOT_CANCEL_PAY(HttpStatus.BAD_REQUEST, "결제를 취소할 수 없습니다."),
    NOT_EQUAL_MERCHANT_ID(HttpStatus.BAD_REQUEST, "주문번호 ID가 일치하지 않습니다."),
    ALREADY_DELIVERY_STATUS(HttpStatus.BAD_REQUEST, "이미 배송 중입니다."),


    /**
     * ********************************************* GLOBAL ***********************************************************
     */

    TEST(HttpStatus.INTERNAL_SERVER_ERROR, "테스트 에러"),
    FAIL_SERIALIZE_OBJECT_INTO_JSON(HttpStatus.BAD_REQUEST, "객체를 JSON으로 변환할 수 없습니다."),
    FAIL_DESERIALIZE_JSON_INTO_OBJECT(HttpStatus.BAD_REQUEST, "JSON을 객체로 변환할 수 없습니다."),
    SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");

    private final HttpStatus status;
    private final String message;
    // final 키워드는 Java에서 변수의 값을 한 번 할당하면 변경할 수 없음을 나타내는 데 사용
    // 오류 코드를 나타내는 enum의 경우 불변성을 유지하는 것이 특히 중요. 왜냐하면 오류 코드는 프로그램 실행 동안 일반적으로 변경되지 않기 때문. 이러한 값의 수정 시도는 예상치 못한 동작 및 프로그램 오류로 이어질 수 있음
}
