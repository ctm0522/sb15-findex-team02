# 2조
[노션 페이지](https://app.notion.com/p/ee89c8524dc58272aad1010f305f3a4b?source=copy_link)
## 팀원 구성
| 이름 | GitHub |
|:---:|:---|
| (최태민) | [Taemin02](https://github.com/ctm0522) |
| (김민규) | [kmg-git](https://github.com/kmg-git) |
| (김한민) | [Miiiin06](https://github.com/Miiiin06) |

## 프로젝트 소개
- 금융위원회 주가 지수 데이터를 연동하여 시계열 차트와 성과 분석 정보를 제공하는 Spring 백엔드 시스템 개발
- 프로젝트 기간: 2026.10.14 ~ 2026.10.23

## 기술 스택
- Backend: Spring Boot, Spring Security, Spring Data JPA
- Database: PostgreSQL
- 공통 Tool: Git & Github, Discord

## 팀원별 구현 기능 상세
최태민: 외부 API 통신 Client 구현, 외부 API에 필요한 DTO 설계, 자동 연동 설정 공동 구현

김민규: 각 도메인 별 Service, Repository 구현

김한민: 각 도메인별 Controller 구현, 자동 연동 설정 공동 구현

## 파일 구조
```
src
 ┗ main
   ┗ java
     ┗ org.findex.team2.sb15findexteam02
       ┣ controller # 컨트롤러
       ┣ dto        # DTO
       ┃ ┣ request  # 요청 DTO
       ┃ ┗ response # 응답 DTO
       ┣ exception  # 예외처리
       ┣ model      # 엔티티
       ┣ repository # 레포지토리
       ┣ service    # 서비스
       ┗ Sb15FindexTeam02Application.java
```
처음 시작하는 소규모 프로젝트 이므로, 각자의 실력과 현 상황을 고려하여 레이어 기반 설계 적용

## 구현 홈페이지
(링크)

## 프로젝트 회고록


