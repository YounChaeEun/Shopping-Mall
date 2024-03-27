# ShoppingMall
Spring Boot로 만든 쇼핑몰 프로젝트입니다. <br>
어느 곳에서든 사용하며 접할 수 있는 인터넷 쇼핑몰들을 모티브하여 설계하였습니다.<br>
사용자, 판매자, 관리자의 권한이 있는 쇼핑몰 API입니다.

# 기술 스택
- Language: Java
- JDK: 17
- Framework: Spring Boot 2.7.16
- ORM: Spring Data JPA
- Security: Spring Security
- DB: MySQL, AWS S3
- Server: AWS EC2
- Test: JUnit5, AssertJ
- Build Tool: Gradle

# REST API 문서
- [ShoppingMall API 문서](https://github.com/LeeDaye7888/ShoppingMall/issues/26)


# 기능 설명
( ▶ 를 누르면 간략한 기능 리스트가 나옵니다. )
<details>
<summary>회원</summary>
  
- Spring Security 회원가입 및 로그인
  + 이메일 중복 체크
  + JWT 토큰
  + 로그인 시 Access Token, Refresh Token 발급
  + Refresh Token 이용해서 Access Token 재발급
- 자신의 회원 정보 조회
- 회원 정보 수정
- 회원 탈퇴
  + 전체 사용자: 회원의 장바구니, refresh token, 권한 삭제
  + 판매자: 사용자의 장바구니에 존재하는 판매자 판매 상품 삭제
  + 관리자: 해당 없음
- 비밀번호 변경
- (관리자) 회원 정보 전체 조회
</details>

<details>
<summary>주문</summary>
  
  - 주문번호 생성(UUID)
  - 주문 등록
    + 주문 수량 > 주문하려는 상품 재고 시, 주문 불가
    + 품절/판매중단인 상품 주문 불가
    + 주문 수량만큼 해당 상품 재고 감소
    + 총 주문 금액의 1% 적립금 부여
    + 주문 상품이 장바구니에 존재할 경우, 장바구니 DB에서 삭제 
  - 주문 취소(결제 취소)
    + 이미 취소한 결제 다시 취소 불가
    + 상품이 배송 중일 경우 취소 불가
    + 결제 회원과 다른 회원이 대신 결제 취소 불가
  - 주문 전체 조회
  - 주문 상세 조회
</details>

<details>
<summary>상품</summary>

  - 상품 등록(판매자)
    + 상품 이미지는 1장 이상 필수 등록
    + 상품 이미지들은 AWS S3에 저장
    + 이미 존재하는 동일한 이름으로 상품 등록 불가
    + 상품 옵션 추가는 필수 X
  - 상품 수정(판매자)
    + 사이트에 이미 존재하는 상품명으로 상품 수정 불가
  - 상품 전체 조회(판매자)
  - 상품 삭제(판매자)
  - 상품 상세 조회(전체 사용자)
  - 상품 전체 조회(전체 사용자)
</details>

<details>
<summary>장바구니</summary>

  - 장바구니 생성
    + 장바구니에 담을 상품 수량 > 상품 재고 시, 장바구니에 등록 불가
    + 품절/판매중단인 상품 장바구니에 등록 불가
    + 장바구니에 이미 존재하는 상품이면 재등록 불가
  - 장바구니 수정
  - 회원에 해당하는 장바구니 전체 조회
  - 선택한 장바구니들 다중 삭제
</details>

<details>
<summary>리뷰</summary>

  - 리뷰 등록
      + 주문 완료 후 14일이내에 리뷰 등록 가능
  - 리뷰 수정
  - 리뷰 삭제
  - (상품 상세조회) 리뷰 조회
  - (마이페이지) 리뷰 조회
</details>

<details>
<summary>카테고리</summary>

  - 카테고리 생성(관리자)
    + 이미 존재하는 동일한 이름으로 카테고리 등록 불가
  - 카테고리 수정(관리자)
  - 카테고리 조회(전체 사용자)
  - 카테고리 삭제(관리자)
    + 카테고리내에 상품이 존재할 시 카테고리 삭제 불가
</details>





# ERD
![쇼핑몰_완성본 (1)](https://github.com/LeeDaye7888/ShoppingMall/assets/111855256/42bb69bc-905d-4b30-9aaf-8477ae1da1d7)

