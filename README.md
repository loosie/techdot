# TechDot 기술 블로그 큐레이션 서비스


## 계층별 로직
<img width="799" alt="스크린샷 2022-03-02 오후 6 24 00" src="https://user-images.githubusercontent.com/54282927/156333042-d24e9c14-2951-4a78-a4cb-b080182dff79.png">


<br>


## 테스트 코드 작성 규칙
### Controller단 테스트 (mockMvc)
mvc를 처리하는 controller 로직은 mockMVC로 작성하기
- 전송되는 model, error, view 조회

<br>

### Service단 테스트 (Mockito)
ResponseEntity를 반환하는 api controller 로직은 mockito로 작성하기
- mockito로 가짜 반환 객체 생성후 비즈니스 로직 테스트

<br>

### Repository단 테스트 (DataJpaTest)
- 정상적인 도메인 생성 테스트 (save)
- 직접 생성한 네임드 쿼리 테스트 (find~, exists~, ...)
커스텀 쿼리 - 클래스 따로 생성해서 테스트

<br>

### Domain단 테스트
도메인 값을 수정하는 도메인 비즈니스 로직 작성
도메인 생성 에러 (null) 테스트

<br>

---
© loosie
