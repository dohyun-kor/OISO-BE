# 공통 API 응답 및 오류 사용 가이드

## 1. 응답 규격

모든 신규 API와 수정하는 기존 API는 성공 여부와 관계없이 `code`, `message`, `data` 필드를 사용한다.

### 성공 응답

성공 응답의 애플리케이션 코드는 항상 `0`이고 기본 메시지는 `정상 처리 되었습니다.`이다.

```json
{
  "code": 0,
  "message": "정상 처리 되었습니다.",
  "data": {
    "contents": {
      "sample": 1,
      "sample2": 2
    },
    "others": {
      "sample": 1,
      "sample2": 2
    }
  }
}
```

응답할 데이터가 없더라도 `data` 필드는 생략하지 않고 `null`로 반환한다.

```json
{
  "code": 0,
  "message": "정상 처리 되었습니다.",
  "data": null
}
```

### 오류 응답

오류의 `code`는 HTTP 상태 코드가 아닌 애플리케이션 오류 코드다. HTTP 상태는 응답 상태 줄에서 별도로 전달한다.

```http
HTTP/1.1 404 Not Found
Content-Type: application/json
```

```json
{
  "code": 3001,
  "message": "요청한 리소스를 찾을 수 없습니다.",
  "data": null
}
```

오류 응답의 `data`는 항상 `null`이다. 예외 메시지, 스택 트레이스와 같은 서버 내부 정보는 응답에 포함하지 않는다.

## 2. 성공 응답 사용법

성공 응답은 `CommonResponse<T>`와 정적 팩토리 메서드 `success`를 사용한다.

### 데이터가 있는 응답

```java
import com.nigahae.common.response.CommonResponse;

@GetMapping("/{userId}")
public ResponseEntity<CommonResponse<UserResponse>> getUser(@PathVariable Long userId) {
    UserResponse response = userService.getUser(userId);
    return ResponseEntity.ok(CommonResponse.success(response));
}
```

HTTP 상태를 별도로 지정할 필요가 없다면 `CommonResponse`를 직접 반환해도 된다.

```java
@GetMapping
public CommonResponse<List<UserResponse>> getUsers() {
    return CommonResponse.success(userService.getUsers());
}
```

### 데이터가 없는 응답

```java
@DeleteMapping("/{userId}")
public ResponseEntity<CommonResponse<Void>> deleteUser(@PathVariable Long userId) {
    userService.deleteUser(userId);
    return ResponseEntity.ok(CommonResponse.success());
}
```

성공 응답에서 도메인 객체나 컬렉션을 그대로 반환하지 않는다.

```java
// 사용하지 않는다.
public List<User> getUsers() {
    return userService.getUsers();
}
```

## 3. 오류 코드 규칙

오류 코드는 `범주 1자리 + 도메인 3자리`로 구성된 4자리 정수다.

| 범주 | 코드 범위 | 용도 |
| --- | --- | --- |
| `VALIDATION` | `1xxx` | 요청 파라미터, 요청 본문 또는 값 검증 실패 |
| `BUSINESS` | `2xxx` | 중복, 제한 초과 등 비즈니스 규칙상 처리 불가 |
| `RESOURCE` | `3xxx` | 요청한 리소스를 찾을 수 없음 |
| `AUTH` | `4xxx` | 인증 또는 권한 문제 |
| `SERVER` | `5xxx` | 예상하지 못한 서버 처리 실패 |

현재 도메인 번호는 다음과 같다.

| 도메인 | 세부 번호 | 예시 |
| --- | ---: | ---: |
| 공통 | `001` | 검증 오류 `1001`, 리소스 없음 `3001` |
| security | `002` | Refresh Token 오류 `4002` |

같은 범주와 도메인에 속한 오류는 같은 코드를 사용할 수 있으며 구체적인 원인은 `message`로 구분한다. 예를 들어 Refresh Token 누락과 유효하지 않은 Refresh Token은 모두 `4002`다.

`user`, `tourdata` 등 새로운 도메인 오류를 추가할 때는 `ErrorDomain`에 사용하지 않은 번호를 먼저 등록한다. 다음 번호는 `003`부터 순차적으로 사용한다.

```java
public enum ErrorDomain {
    COMMON(1),
    SECURITY(2),
    USER(3);
}
```

그다음 `ErrorCode`에 범주, 도메인, HTTP 상태, 사용자에게 보여 줄 메시지를 등록한다.

```java
USER_NOT_FOUND(
        ErrorCategory.RESOURCE,
        ErrorDomain.USER,
        HttpStatus.NOT_FOUND,
        "존재하지 않는 사용자입니다."
)
```

위 오류의 응답 코드는 `RESOURCE 3000 + USER 003`, 즉 `3003`이 된다.

## 4. 오류 발생 방법

비즈니스 또는 리소스 오류가 발생하면 컨트롤러에서 `CommonError`를 직접 만들지 않는다. 서비스에서 `CustomException`을 발생시키고 `GlobalExceptionHandler`가 공통 형식으로 변환하도록 한다.

```java
import com.nigahae.common.constant.ErrorCode;
import com.nigahae.common.exception.CustomException;

public UserResponse getUser(Long userId) {
    User user = userRepository.findById(userId)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

    return UserResponse.from(user);
}
```

### 요청값 검증

요청 DTO에 Bean Validation을 선언하고 컨트롤러 파라미터에 `@Valid`를 사용한다.

```java
public record CreateUserRequest(
        @NotBlank(message = "이름은 필수입니다.")
        String name
) {
}

@PostMapping
public CommonResponse<UserResponse> createUser(
        @Valid @RequestBody CreateUserRequest request
) {
    return CommonResponse.success(userService.createUser(request));
}
```

검증에 실패하면 첫 번째 검증 메시지를 사용해 HTTP `400`, 코드 `1001`로 응답한다.

```json
{
  "code": 1001,
  "message": "이름은 필수입니다.",
  "data": null
}
```

검증 메시지를 추출할 수 없으면 `요청 값이 올바르지 않습니다.`를 사용한다. 요청 파라미터 누락, 타입 불일치, 읽을 수 없는 JSON도 동일하게 `1001`로 처리한다.

### 예상하지 못한 서버 오류

처리되지 않은 예외는 HTTP `500`, 코드 `5001`로 응답한다.

```json
{
  "code": 5001,
  "message": "서버 내부 오류가 발생했습니다.",
  "data": null
}
```

실제 예외와 스택 트레이스는 서버 로그에만 기록된다. 따라서 일반 예외를 잡아서 `exception.getMessage()`를 클라이언트에 반환하지 않는다.

## 5. 프론트엔드 처리 기준

프론트엔드는 HTTP 상태와 애플리케이션 오류 코드를 함께 확인한다.

- HTTP 상태: 재인증, 화면 이동, 재시도 여부 같은 전송 수준의 처리
- `code`: 오류 범주와 도메인에 따른 사용자 안내 및 화면 처리
- `message`: 사용자에게 표시할 구체적인 안내 문구

```javascript
const response = await fetch('/api/auth/refresh', {
  method: 'POST',
  credentials: 'include',
});

const body = await response.json();

if (!response.ok && body.code === 4002) {
  alert(body.message);
}
```

## 6. 테스트 및 확인

공통 응답 관련 테스트만 실행한다.

```powershell
cd C:\kdy\BE\oiso
./gradlew.bat :common:test
```

전체 프로젝트 회귀 테스트를 실행한다.

```powershell
./gradlew.bat test
```

애플리케이션 실행 후 Refresh Token 없이 호출하면 실제 오류 응답을 확인할 수 있다.

```powershell
curl.exe -i -X POST http://localhost:8080/api/auth/refresh
```

예상 결과는 HTTP `401`과 다음 본문이다.

```json
{
  "code": 4002,
  "message": "Refresh Token이 없습니다.",
  "data": null
}
```
