# 프론트엔드용 카카오 로그인 연동 가이드

## 1. 현재 구현 범위

현재 백엔드는 카카오 OAuth2 로그인을 지원한다. 카카오 인증 성공 후 회원 정보를 DB에 저장하고 JWT access/refresh token을 브라우저의 HttpOnly 쿠키로 발급한다.

- 소셜 공급자: 카카오
- 회원 식별 기준: `provider=kakao` + 카카오 사용자 ID
- 이메일: 선택 정보이며 카카오 계정이 제공하지 않아도 로그인 가능
- 토큰 전달 방식: 응답 body나 URL이 아닌 HttpOnly 쿠키
- 로그인 성공 후 이동 주소: 운영 환경변수 `PROD_OAUTH2_REDIRECT_URI`
- 로컬 프론트 주소: `http://localhost:3000`
- 로컬 백엔드 주소: `http://localhost:8080`
- 운영 프로필 연동 테스트 프론트 주소: `http://localhost:5173`
- 운영 백엔드 주소: `https://oiso.duckdns.org`

프론트엔드는 access token이나 refresh token 값을 직접 읽거나 저장하지 않는다. 브라우저가 쿠키를 자동으로 전송하도록 요청에 credentials 옵션만 설정한다.

## 2. 백엔드 API 계약

### 카카오 로그인 시작

```http
GET /oauth2/authorization/kakao
```

로컬 전체 주소:

```text
http://localhost:8080/oauth2/authorization/kakao
```

이 주소는 `fetch`나 Axios로 호출하지 말고 브라우저 페이지 이동으로 열어야 한다.

```javascript
window.location.href = 'http://localhost:8080/oauth2/authorization/kakao';
```

카카오 인증이 끝나면 백엔드 callback에서 회원 저장과 쿠키 발급을 처리한 후 `http://localhost:3000/`으로 리다이렉트한다.

### access token 재발급

```http
POST /api/auth/refresh
```

- 요청 body: 없음
- refresh token: 브라우저 쿠키로 자동 전송
- 성공: `200 OK`, 새 access token 쿠키 발급
- refresh 쿠키 없음: `401 Unauthorized`, 오류 코드 `4002`
- 만료·변조·잘못된 타입: `401 Unauthorized`

### 로그아웃

```http
POST /api/auth/logout
```

- 요청 body: 없음
- access token 인증 필요
- 성공: `200 OK`, access/refresh 쿠키 만료

### 로그인 상태 및 현재 사용자 확인

```http
GET /api/auth/me
```

access token이 유효하면 `200 OK`와 현재 사용자를 반환하고, 없거나 유효하지 않으면 로그인 페이지 리다이렉트가 아닌 `401 Unauthorized`를 반환한다.

```json
{
  "userId": 3,
  "nickname": "사용자"
}
```

전체 회원 목록은 `GET /api/users`이며 로그인 상태 확인 용도로 사용하지 않는다.

## 3. 프론트엔드 구현 예시

### Fetch 공통 함수

```javascript
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080';

async function apiFetch(path, options = {}) {
  return fetch(`${API_BASE_URL}${path}`, {
    ...options,
    credentials: 'include',
    headers: {
      'Content-Type': 'application/json',
      'X-Requested-With': 'XMLHttpRequest',
      ...options.headers,
    },
  });
}
```

`credentials: 'include'`가 없으면 프론트와 백엔드의 포트가 다를 때 쿠키가 정상적으로 전송되지 않을 수 있다. `X-Requested-With`는 일반 브라우저 이동과 API 요청을 구분해 인증 실패를 API 응답으로 받기 위해 사용한다.

### Axios 인스턴스

```javascript
import axios from 'axios';

export const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080',
  withCredentials: true,
  headers: { 'X-Requested-With': 'XMLHttpRequest' },
});
```

### 로그인 버튼

```jsx
function KakaoLoginButton() {
  const login = () => {
    const apiBaseUrl = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080';
    window.location.assign(
      `${apiBaseUrl}/oauth2/authorization/kakao`
    );
  };

  return <button onClick={login}>카카오로 로그인</button>;
}
```

### access token 재발급

```javascript
async function refreshAccessToken() {
  const response = await apiFetch('/api/auth/refresh', {
    method: 'POST',
  });

  if (!response.ok) {
    throw new Error('로그인 세션이 만료되었습니다.');
  }
}
```

### 401 응답 시 한 번만 재시도

```javascript
async function authenticatedFetch(path, options = {}) {
  let response = await apiFetch(path, options);

  if (response.status !== 401) {
    return response;
  }

  const refreshResponse = await apiFetch('/api/auth/refresh', {
    method: 'POST',
  });

  if (!refreshResponse.ok) {
    window.location.assign('/login');
    return response;
  }

  return apiFetch(path, options);
}
```

무한 재시도를 막기 위해 최초 API 요청당 refresh는 한 번만 호출한다. 여러 API가 동시에 401을 반환할 경우 refresh 요청을 하나로 합치는 처리는 프론트 공통 API 모듈에서 추가하는 것이 좋다.

### 로그아웃

```javascript
async function logout() {
  const response = await apiFetch('/api/auth/logout', {
    method: 'POST',
  });

  if (response.ok) {
    window.location.assign('/login');
  }
}
```

## 4. 로컬 연동 설정

백엔드 `.env`의 핵심 설정은 다음과 같다.

```env
SPRING_PROFILES_ACTIVE=local
LOCAL_KAKAO_REDIRECT_URI={baseUrl}/login/oauth2/code/kakao
LOCAL_OAUTH2_REDIRECT_URI=http://localhost:3000/
LOCAL_CORS_ALLOWED_ORIGIN=http://localhost:3000
LOCAL_COOKIE_SECURE=false
LOCAL_COOKIE_SAME_SITE=Lax
```

카카오 개발자 콘솔의 REST API 키 Redirect URI에는 다음 실제 주소를 등록한다.

```text
http://localhost:8080/login/oauth2/code/kakao
```

설정을 변경한 경우 백엔드를 완전히 재시작해야 한다.

## 5. 프론트엔드 테스트 순서

1. PostgreSQL과 백엔드를 실행하고 `http://localhost:8080/swagger-ui.html` 접속을 확인한다.
2. 프론트엔드를 `http://localhost:3000`에서 실행한다.
3. 로그인 버튼을 눌러 카카오 로그인 화면으로 이동하는지 확인한다.
4. 카카오 로그인 및 동의 후 `http://localhost:3000/`으로 돌아오는지 확인한다.
5. 브라우저 개발자 도구의 Application/Storage > Cookies에서 `accessToken`, `refreshToken` 이름이 보이는지 확인한다. HttpOnly 쿠키이므로 JavaScript에서 값은 읽히지 않는 것이 정상이다.
6. 보호 API 요청에 `credentials: include`가 적용되고 성공하는지 확인한다.
7. `POST /api/auth/refresh` 호출 후 `200` 응답과 access 쿠키 재발급을 확인한다.
8. 같은 카카오 계정으로 재로그인하고 DB 회원 행이 중복 생성되지 않는지 확인한다.
9. 이메일 제공에 동의하지 않은 계정도 로그인되는지 확인한다.
10. 로그아웃 후 보호 API가 더 이상 성공하지 않는지 확인한다.

DB 확인 SQL:

```sql
SELECT user_id,
       provider,
       provider_user_id,
       nickname,
       email,
       profile_image_url
FROM public."User"
ORDER BY user_id DESC;
```

## 6. 운영 환경 체크리스트

현재 운영 프로필은 로컬 Vite 프론트와 연동 테스트하기 위해 다음 값을 사용한다.

```env
SPRING_PROFILES_ACTIVE=prod
PROD_KAKAO_REDIRECT_URI=https://oiso.duckdns.org/login/oauth2/code/kakao
PROD_OAUTH2_REDIRECT_URI=http://localhost:5173
PROD_CORS_ALLOWED_ORIGIN=http://localhost:5173
PROD_COOKIE_SECURE=true
PROD_COOKIE_SAME_SITE=None
```

카카오 개발자 콘솔에도 운영 callback URI를 별도로 등록한다.

```text
https://oiso.duckdns.org/login/oauth2/code/kakao
```

localhost 프론트와 DuckDNS API는 서로 다른 사이트이므로 `PROD_COOKIE_SAME_SITE=None`과 `PROD_COOKIE_SECURE=true`가 필요하다. 브라우저의 서드파티 쿠키 차단 정책에 따라 쿠키 인증이 제한될 수 있으므로 Chrome, Safari 모바일 환경을 포함해 실제 브라우저에서 확인한다.

Vite 프론트 환경변수에는 다음 값을 등록한다.

```env
VITE_API_BASE_URL=https://oiso.duckdns.org
```

프레임워크가 Next.js라면 공개 환경변수 이름을 `NEXT_PUBLIC_API_BASE_URL`로 바꾸고 API 클라이언트에서도 같은 이름을 사용한다.

## 7. 자주 발생하는 문제

### 로그인 후 잘못된 프론트 주소로 이동

백엔드의 `PROD_OAUTH2_REDIRECT_URI`를 확인하고 백엔드를 재시작한다.

### `redirect_uri_mismatch` 또는 카카오 `KOE006`

백엔드가 카카오에 전달한 callback 주소와 카카오 개발자 콘솔의 Redirect URI가 글자 단위로 같아야 한다. 프로토콜, 포트, 경로, 마지막 슬래시 차이를 확인한다.

### 로그인은 성공했지만 API가 인증 실패

- 프론트 요청에 `credentials: 'include'` 또는 Axios `withCredentials: true`가 있는지 확인한다.
- 백엔드 CORS origin이 프론트 주소와 정확히 같은지 확인한다.
- 로컬 HTTP에서 `LOCAL_COOKIE_SECURE=false`인지 확인한다.
- 운영 HTTPS에서 `PROD_COOKIE_SECURE=true`인지 확인한다.
- access token 만료 후 refresh API를 호출했는지 확인한다.

### JavaScript에서 토큰이 보이지 않음

정상이다. 토큰은 XSS 노출을 줄이기 위해 HttpOnly 쿠키로 발급되며 `document.cookie`로 읽을 수 없다.

### Swagger 접속 시 로그인 화면으로 이동

현재 `/swagger-ui.html`, `/swagger-ui/**`, `/v3/api-docs/**`는 로컬과 운영 모두 공개되어 있다. 이전 서버가 실행 중이거나 배포가 반영되지 않았는지 확인한다.

## 8. 백엔드 담당자에게 전달할 테스트 결과

오류를 공유할 때는 다음 정보를 함께 전달한다.

- 요청한 프론트 URL과 백엔드 URL
- 실패한 API 경로와 HTTP 상태 코드
- Network 탭의 Request URL, Response Headers, Redirect Location
- 쿠키 이름과 Secure/HttpOnly/SameSite 속성 여부(토큰 값 자체는 공유 금지)
- 카카오 오류 코드
- 재현 순서
- 백엔드 실행 프로필(local/prod)

JWT, 카카오 Client Secret, refresh/access token 원문은 채팅이나 이슈에 첨부하지 않는다.
