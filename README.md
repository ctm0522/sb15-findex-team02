# 2조
(협업 문서)

## 팀원 구성
최태민

김민규

김한민

진하윤

## 프로젝트 소개
- 금융위원회 주가 지수 데이터를 연동하여 시계열 차트와 성과 분석 정보를 제공하는 Spring 백엔드 시스템 개발
- 프로젝트 기간: 2026.10.14 ~ 2026.10.23

## 기술 스택
- Backend: Spring Boot, Spring Security, Spring Data JPA
- Database: PostgreSQL
- 공통 Tool: Git & Github, Discord

## 팀원별 구현 기능 상세
최태민:

김민규:

김한민:

진하윤:

## 파일 구조
src
 ┣ main
 ┃ ┣ java
 ┃ ┃ ┣ com
 ┃ ┃ ┃ ┣ example
 ┃ ┃ ┃ ┃ ┣ controller
 ┃ ┃ ┃ ┃ ┃ ┣ AuthController.java
 ┃ ┃ ┃ ┃ ┃ ┣ UserController.java
 ┃ ┃ ┃ ┃ ┃ ┗ AdminController.java
 ┃ ┃ ┃ ┃ ┣ model
 ┃ ┃ ┃ ┃ ┃ ┣ User.java
 ┃ ┃ ┃ ┃ ┃ ┗ Course.java
 ┃ ┃ ┃ ┃ ┣ repository
 ┃ ┃ ┃ ┃ ┃ ┣ UserRepository.java
 ┃ ┃ ┃ ┃ ┃ ┗ CourseRepository.java
 ┃ ┃ ┃ ┃ ┣ service
 ┃ ┃ ┃ ┃ ┃ ┣ AuthService.java
 ┃ ┃ ┃ ┃ ┃ ┣ UserService.java
 ┃ ┃ ┃ ┃ ┃ ┗ AdminService.java
 ┃ ┃ ┃ ┃ ┣ security
 ┃ ┃ ┃ ┃ ┃ ┣ SecurityConfig.java
 ┃ ┃ ┃ ┃ ┃ ┗ JwtAuthenticationEntryPoint.java
 ┃ ┃ ┃ ┃ ┣ dto
 ┃ ┃ ┃ ┃ ┃ ┣ LoginRequest.java
 ┃ ┃ ┃ ┃ ┃ ┗ UserResponse.java
 ┃ ┃ ┃ ┃ ┣ exception
 ┃ ┃ ┃ ┃ ┃ ┣ GlobalExceptionHandler.java
 ┃ ┃ ┃ ┃ ┃ ┗ ResourceNotFoundException.java
 ┃ ┃ ┃ ┃ ┣ utils
 ┃ ┃ ┃ ┃ ┃ ┣ JwtUtils.java
 ┃ ┃ ┃ ┃ ┃ ┗ UserMapper.java
 ┃ ┃ ┃ ┣ resources
 ┃ ┃ ┃ ┃ ┣ application.properties
 ┃ ┃ ┃ ┃ ┗ static
 ┃ ┃ ┃ ┃ ┃ ┣ css
 ┃ ┃ ┃ ┃ ┃ ┃ ┗ style.css
 ┃ ┃ ┃ ┃ ┃ ┣ js
 ┃ ┃ ┃ ┃ ┃ ┃ ┗ script.js
 ┃ ┃ ┃ ┣ webapp
 ┃ ┃ ┃ ┃ ┣ WEB-INF
 ┃ ┃ ┃ ┃ ┃ ┗ web.xml
 ┃ ┃ ┃ ┣ test
 ┃ ┃ ┃ ┃ ┣ java
 ┃ ┃ ┃ ┃ ┃ ┣ com
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ example
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ AuthServiceTest.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ UserControllerTest.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┗ ApplicationTests.java
 ┃ ┃ ┃ ┣ resources
 ┃ ┃ ┃ ┃ ┣ application.properties
 ┃ ┃ ┃ ┃ ┗ static
 ┃ ┃ ┃ ┃ ┃ ┣ css
 ┃ ┃ ┃ ┃ ┃ ┃ ┗ style.css
 ┃ ┃ ┃ ┃ ┃ ┣ js
 ┃ ┃ ┃ ┃ ┃ ┃ ┗ script.js
 ┣ pom.xml
 ┣ Application.java
 ┣ application.properties
 ┣ .gitignore
 ┗ README.md

## 구현 홈페이지
(링크)

## 프로젝트 회고록


