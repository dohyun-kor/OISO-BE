# 로컬 카카오 로그인 테스트 페이지

백엔드를 local 프로필로 실행한 뒤 이 디렉터리에서 정적 서버를 3000 포트로 실행한다.

```powershell
python -m http.server 3000
```

브라우저에서 `http://localhost:3000`을 열고 **카카오로 로그인** 버튼을 누른다.

백엔드 `.env`는 다음 값이어야 한다.

```env
SPRING_PROFILES_ACTIVE=local
LOCAL_KAKAO_REDIRECT_URI={baseUrl}/login/oauth2/code/kakao
LOCAL_OAUTH2_REDIRECT_URI=http://localhost:3000/
```

카카오 개발자 콘솔에는 다음 Redirect URI가 등록되어 있어야 한다.

```text
http://localhost:8080/login/oauth2/code/kakao
```
