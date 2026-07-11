# 카카오 소셜 로그인 테스트

## 사전 설정

1. 카카오 개발자 콘솔의 Redirect URI에 다음 값을 등록한다.
   `http://localhost:8080/login/oauth2/code/kakao`
2. 동의 항목에서 닉네임과 프로필 이미지를 활성화한다. 이메일은 선택 항목이어도 된다.
3. `.env.example`을 참고해 실제 비밀값을 운영체제 환경변수로 설정한다.
4. 애플리케이션 시작 시 기존 `public."User"` 데이터와 여행 설정 컬럼은 유지되고 소셜 로그인 컬럼과 PK 시퀀스만 추가된다.

## 실행

Java 17과 PostgreSQL을 준비한 뒤 `app` 모듈을 로컬 프로필로 실행한다.

```powershell
$env:SPRING_PROFILES_ACTIVE='local'
$env:LOCAL_KAKAO_CLIENT_ID='발급받은 REST API 키'
$env:LOCAL_KAKAO_CLIENT_SECRET='발급받은 Client Secret'
$env:LOCAL_JWT_SECRET_KEY='32바이트 이상의 임의 문자열'
./gradlew.bat :app:bootRun
```

브라우저에서 `http://localhost:8080/oauth2/authorization/kakao`에 접속한다. 로그인 성공 후 `http://localhost:3000/`으로 이동해야 한다.

## 확인 항목

- 브라우저 저장소에 `accessToken`, `refreshToken` 쿠키가 있고 `HttpOnly`, `SameSite=Lax`가 설정된다.
- 로컬 HTTP에서는 `Secure=false`, 운영 HTTPS에서는 `Secure=true`이다.
- `public."User"`에 `provider=kakao`와 카카오 사용자 ID가 저장된다.
- 같은 계정으로 다시 로그인해도 행이 추가되지 않고 프로필만 갱신된다.
- 이메일 제공에 동의하지 않은 계정도 로그인된다.
- `POST /api/auth/refresh`는 refresh 쿠키로 새 access 쿠키를 발급한다.
- `POST /api/auth/logout`은 두 쿠키를 모두 만료시킨다.
- access 쿠키가 있으면 `GET /users` 같은 보호 API가 성공하고 없으면 401/403 응답을 반환한다.

자동 테스트는 다음 명령으로 실행한다.

```powershell
./gradlew.bat test
```
