# 사용자 관리 시스템

이 프로젝트는 사용자 관리 기능을 제공하는 RESTful API 서비스입니다. 사용자 등록, 로그인, 권한 관리 등의 기능을 제공합니다.

## 주요 기능

1. 사용자 등록 (일반 사용자 및 관리자)
2. 사용자 로그인 및 JWT 토큰 발급
3. 역할 기반 접근 제어 (RBAC)
4. 사용자 역할 변경 (관리자 전용)

## 기술 스택

- Java
- Spring Boot
- Spring Security
- JWT (JSON Web Token)
- 인메모리 저장소 (ConcurrentHashMap)

## API 엔드포인트

### 1. 일반 사용자 회원가입
- **URL**: `/signup`
- **Method**: POST
- **Request Body**:
  ```json
  {
    "userName": "testuser",
    "password": "password123",
    "nickName": "tester"
  }
  ```
- **Response**: 
  ```json
  {
    "message": "success",
    "code": 200,
    "data": {
      "userName": "testuser",
      "nickName": "tester",
      "roles": {
        "role": "USER"
      }
    }
  }
  ```

### 2. 관리자 회원가입
- **URL**: `/admin/signup`
- **Method**: POST
- **Request Body**:
  ```json
  {
    "userName": "testadmin",
    "password": "password123",
    "nickName": "admin"
  }
  ```
- **Response**: 
  ```json
  {
    "message": "success",
    "code": 200,
    "data": {
      "userName": "testadmin",
      "nickName": "admin",
      "roles": {
        "role": "ADMIN"
      }
    }
  }
  ```

### 3. 로그인
- **URL**: `/login`
- **Method**: POST
- **Request Body**:
  ```json
  {
    "userName": "testuser",
    "password": "password123"
  }
  ```
- **Response**: 
  ```json
  {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "role": "USER"
  }
  ```
- **Response Headers**:
  - `Authorization`: Bearer [JWT 토큰]

### 4. 사용자 역할 변경 (관리자 전용)
- **URL**: `/admin/users/{userId}/roles`
- **Method**: PATCH
- **Headers**:
  - `Authorization`: Bearer [JWT 토큰]
- **Path Parameters**:
  - `userId`: 사용자 ID
- **Request Body**:
  ```json
  "ADMIN"
  ```
- **Response**: 
  ```json
  {
    "message": "success",
    "code": 200,
    "data": {
      "userName": "testuser",
      "nickName": "tester",
      "roles": {
        "role": "ADMIN"
      }
    }
  }
  ```

## 오류 코드

| 코드 | 메시지 | HTTP 상태 |
|------|--------|-----------|
| 400 | 잘못된 요청입니다. | Bad Request |
| 401 | 인증이 필요합니다. | Unauthorized |
| 403 | 권한이 없습니다. | Forbidden |
| 409 | 이미 등록된 사용자입니다. | Conflict |

## 프로젝트 실행 방법

1. 환경 변수 설정
   - `JWT_KEY`: JWT 서명에 사용할 시크릿 키

2. 애플리케이션 실행
   ```bash
   ./gradlew bootRun
   ```

3. 테스트 실행
   ```bash
   ./gradlew test
   ```

## 보안 구현 내용

1. **JWT 기반 인증**
   - 로그인 성공 시 JWT 토큰 발급
   - 토큰에 사용자 권한 정보 포함

2. **역할 기반 접근 제어**
   - `@Secured` 어노테이션을 사용한 메서드 수준 보안
   - 관리자 전용 엔드포인트 권한 검사

3. **비밀번호 암호화**
   - BCrypt 알고리즘을 사용한 비밀번호 해싱

4. **동시성 제어**
   - `ConcurrentHashMap`을 사용한 스레드 안전한 인메모리 저장소
   - 중복 사용자 등록 방지를 위한 동기화 메커니즘
   - synchronized 키워드로 메서드 단위 동시성 제어
