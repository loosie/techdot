# TechDot
기술/개발에 관련된 블로그 글 및 영상 큐레이션 서비스
- 현재 alpha 테스트 중...

<br>

# 실행 방법
## IDE에서 실행 방법
해당 프로젝트는 gradle로 빌드하고 App.java를 실행해야 합니다.
- gradle로 빌드를 해야 querydsl 관련 코드를 생성합니다.
### gradle로 빌드하는 방법
~~~
$ ./gradlew clean build
~~~

### 콘솔 실행 방법
~~~
$ ./gradlew clean build
$ java -jar build/libs/techdot-0.0.1-SNAPSHOT.jar
~~~

### local DB 설정하는 방법 (H2)
- 로컬에서 H2를 실행
- `jdbc:h2~/techdot_local` 파일 모드 데이터베이스 생성


<br>

## 사용 스택
- Java 11, SpringBoot 2.6.4, Gradle7.4.1, Querydsl, MySQL  
- Thymeleaf, BootStrap4.6.1, JQuery

<br>

## 패키지 관리
<img width="512" alt="스크린샷 2022-03-21 오후 1 10 32" src="https://user-images.githubusercontent.com/54282927/159204339-fae9a799-77f6-4f83-a45f-c329630a00ee.png">

<br>

## 테스트 코드 작성 규칙
### Controller단 테스트 (MockMvcTest)
Jsp View를 처리하는 API는 @SpringBootTest, @AutoConfigureMockMvc, TestContainer로 통합 테스트 진행
- jsp view로 반환되는 model, error, view 테스트


<br>

### Service단 테스트 (Mockito)
Ajax와 통신하는 JsonView를 처리하는 API는 Mockito 라이브러리를 확장하여 단위 테스트 진행
- mockito로 가짜 객체 생성후 비즈니스 로직 테스트

<br>

### Repository단 테스트 (DataJpaTest)
JpaRepository를 확장한 Repository클래스는 @DataJpaTest로 자동 롤백 단위 테스트 진행

<br>

### Domain단 테스트 (POJO)
Juni5 라이브러리 만으로 도메인 생성 및 비즈니스 로직 단위 테스트 진행

<br>

---
© loosie
