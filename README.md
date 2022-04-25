# TechDot (alpha v0.0.5)
기술 및 개발 관련 콘텐츠 큐레이션 서비스 

<br>

## 사용 스택
- Back: Java 11, Spring Boot 2.6.4, Spring Security, Spring Data JPA, QueryDsl, MySQL, Redis
- Front: Thymeleaf, BootStrap, Ajax, JQuery
- Test: JUnit5, Mockito, TestContainers, ArchUnit
- DevOps: AWS (EC2, RDS, ElasticCache, S3, CodeDeploy), Jenkins, Nginx

<br>

## 프로젝트 구성도
<img width="920" alt="스크린샷 2022-04-26 오전 3 14 11" src="https://user-images.githubusercontent.com/54282927/165149071-60ef7426-b576-45f1-a092-80404994b679.png">


<br>

## 패키지 구조
<img width="512" alt="스크린샷 2022-03-21 오후 1 10 32" src="https://user-images.githubusercontent.com/54282927/159204339-fae9a799-77f6-4f83-a45f-c329630a00ee.png">

<br>

## 테스트 코드 작성 규칙
- 테스트 코드 작명 규칙: MethodName_{Option}_ExpectedBehavior

### Controller단 테스트 (MockMvcTest)
Controller 층에서는 Jsp View를 반환하는 API를 @MockMvcTest로 통합 테스트 진행
- @MockMvcTest = @SpringBootTest + @AutoConfigureMockMvc + TestContainer

<br>

### Service단 테스트 (Mockito)
Service 층에서는 Json 데이터를 반환하여 Ajax와 통신하는 API를 비즈니스 로직 순서 보장해주는지 Mockito을 통해 단위 테스트 진행 


<br>

### Repository단 테스트 (DataJpaTest)
Repository 층에서는 Spring Data JPA를 사용하여 모든 Repository가 JpaRepository를 확장하였기 때문에 @TCDataJpaTest로 비즈니스 로직에서 사용하는 모든 쿼리 테스트 진행
- @TCDataJpaTest = @DataJpaTest(@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)) + TestContainer 

<br>

### Domain단 테스트 (Junit5)
Domain 층은 도메인을 생성 오류 케이스나 변경해주는 비즈니스 로직을 Junit5만으로 단위 테스트 진행

<br>

## 로컬 실행 방법
### IDE에서 실행 방법
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


---
© loosie
