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

# 📂 패키지 구조
![KakaoTalk_20240403_161030699](https://github.com/LeeDaye7888/ShoppingMall/assets/102869025/b89fdfe7-426f-4abf-93b6-8584639487cd)
<br><br>
# REST API 문서
- [ShoppingMall API 문서](https://github.com/LeeDaye7888/ShoppingMall/issues/26)

![309756971-d03823fb-505e-41af-8399-0ac4ae7fcd10](https://github.com/LeeDaye7888/ShoppingMall/assets/111855256/b8a0a948-9a18-4cfe-983e-d3f5fcf6b7f0)
<br>

# ERD
![쇼핑몰_완성본 (1)](https://github.com/LeeDaye7888/ShoppingMall/assets/111855256/42bb69bc-905d-4b30-9aaf-8477ae1da1d7)
![image](https://github.com/LeeDaye7888/ShoppingMall/assets/111855256/63eb4040-7b8e-4668-a079-5e041f56ad59)
<br><br>
 
# Git 전략 - GitHub Flow
![스크린샷 2024-04-04 184441](https://github.com/LeeDaye7888/ShoppingMall/assets/111855256/9f966cee-f07f-4b52-8a1c-a721b364156e)
<br><br>

# Commit Convention
- **[Feat]**: 새로운 기능 구현
- **[Fix]**: 버그, 오류 해결, 코드 수정
- **[Add]**: Feat 이외의 부수적인 코드 추가
- **[Test]**: 테스트 코드 작성
- **[Refactor]**: 전면 수정이 있을 때 사용
- **[Remove]**: 파일 삭제, 필요없는 코드 삭제
- **[Move]**: 코드의 이동이 있는경우
- **[Style]**: 코드 포맷 변경, 세미콜론 누락, 코드 수정이 없는 경우
- **[Chore]**: 빌드 업무 수정, 패키지 매니저 수정
- **[Docs]**: 문서 개정
- **[Setting]**: 프로젝트 관련 세팅
- **[Comment]**: 필요한 주석 추가 및 변경
<br><br>



