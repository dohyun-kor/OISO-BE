# localhost:5173 카카오 로그인 진단 페이지

배포 백엔드 `https://oiso.duckdns.org`와 로컬 프론트 `http://localhost:5173` 사이의 OAuth 로그인 및 cross-site 쿠키 전송을 확인하는 정적 페이지다.

## 실행

이 디렉터리에서 다음 명령을 실행한다.

```powershell
python -m http.server 5173
```

브라우저에서 `http://localhost:5173`을 연다. 페이지가 열리면 `GET /api/auth/me`를 자동 호출한다.

## 서버 설정

운영 프로필은 다음 값으로 백엔드를 재시작해야 한다.

```env
PROD_OAUTH2_REDIRECT_URI=http://localhost:5173
PROD_CORS_ALLOWED_ORIGIN=http://localhost:5173
PROD_COOKIE_SECURE=true
PROD_COOKIE_SAME_SITE=None
```

카카오 개발자 콘솔 Redirect URI는 프론트 주소가 아니라 백엔드 callback 주소다.

```text
https://oiso.duckdns.org/login/oauth2/code/kakao
```

## 확인 순서

1. **카카오로 로그인**을 누른다.
2. 로그인 후 이 페이지로 돌아오면 `/api/auth/me` 자동 요청 결과를 확인한다.
3. 401이면 F12 Network에서 callback 응답의 `Set-Cookie`와 `/api/auth/me` 요청의 `Cookie` 헤더를 확인한다.
4. **Access Token 재발급**으로 refreshToken 전송 여부를 확인한다.
5. **로그아웃** 후 자동 확인 결과가 401인지 확인한다.

브라우저가 서드파티 쿠키를 차단하면 `SameSite=None; Secure`여도 localhost에서 DuckDNS 쿠키가 전송되지 않을 수 있다. 이 경우 Swagger에서는 성공하지만 이 페이지에서는 401이 발생할 수 있다.
