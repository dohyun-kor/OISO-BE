# 관광지 목록 조회 API

관광지 전체 목록을 페이지 단위로 조회합니다. `keyword`를 전달하면 관광지 이름, 주소,
요약 설명 중 하나에 키워드가 포함된 관광지만 조회합니다.

## Endpoint

```http
GET /tour-spots
```

## Query Parameter

| Name | Type | Required | Default | Constraints | Description |
| --- | --- | --- | --- | --- | --- |
| `keyword` | String | X | - | - | 관광지 이름, 주소, 요약 검색 |
| `page` | Integer | X | `0` | 0 이상 | 페이지 번호 |
| `size` | Integer | X | `20` | 1 이상 100 이하 | 페이지 크기 |

키워드의 앞뒤 공백은 제거합니다. 빈 문자열 또는 공백만 전달하면 키워드가 없는 것으로
처리합니다. 결과는 `spotId` 오름차순으로 정렬합니다.

## Request

Request Body는 없습니다.

### 전체 목록 요청

```http
GET /tour-spots?page=0&size=20
```

### 키워드 검색 요청

```http
GET /tour-spots?keyword=불국사&page=0&size=20
```

## Success Response

- HTTP Status: `200 OK`

```json
{
  "code": 0,
  "message": "관광지 목록 조회에 성공했습니다.",
  "data": {
    "tourSpots": [
      {
        "spotId": 1,
        "name": "불국사",
        "img": "https://oiso.s3.ap-northeast-2.amazonaws.com/tourspot/bulguksa.jpg",
        "overview": "신라 불교문화의 정수를 보여주는 대표 문화유산",
        "address": "경상북도 경주시 불국로 385",
        "like": false
      },
      {
        "spotId": 2,
        "name": "석굴암",
        "img": "https://oiso.s3.ap-northeast-2.amazonaws.com/tourspot/seokguram.jpg",
        "overview": "유네스코 세계문화유산",
        "address": "경상북도 경주시",
        "like": false
      }
    ],
    "page": 0,
    "size": 20,
    "totalCount": 152
  }
}
```

## Response Field

| Field | Type | Nullable | Description |
| --- | --- | --- | --- |
| `code` | Integer | N | 결과 코드. 성공 시 `0` |
| `message` | String | N | 결과 메시지 |
| `data` | Object | N | 목록 조회 결과 |
| `data.tourSpots` | Array | N | 관광지 목록 |
| `data.tourSpots[].spotId` | Long | N | 관광지 ID |
| `data.tourSpots[].name` | String | N | 관광지 이름 |
| `data.tourSpots[].img` | String | Y | 대표 이미지 URL |
| `data.tourSpots[].overview` | String | Y | 관광지 요약 설명 |
| `data.tourSpots[].address` | String | Y | 관광지 주소 |
| `data.tourSpots[].like` | Boolean | N | 즐겨찾기 여부 |
| `data.page` | Integer | N | 현재 페이지 번호 |
| `data.size` | Integer | N | 페이지 크기 |
| `data.totalCount` | Long | N | 검색 조건에 해당하는 전체 관광지 개수 |

> 현재 즐겨찾기 테이블과 인증 기능이 연결되지 않아 `like`는 항상 `false`입니다.
> 즐겨찾기 기능 구현 시 로그인 사용자 기준 값으로 변경합니다.

## Error Response

### 검색 결과가 없는 경우

- HTTP Status: `404 Not Found`
- API Code: `4004`

요청한 페이지에 데이터가 없거나 키워드 검색 결과가 없을 때 반환합니다.

```json
{
  "code": 4004,
  "message": "검색 결과가 존재하지 않습니다.",
  "data": null
}
```

### 잘못된 페이지 요청

- HTTP Status: `400 Bad Request`
- API Code: `4000`

`page`가 음수이거나 정수가 아닌 경우 반환합니다.

```json
{
  "code": 4000,
  "message": "잘못된 요청입니다.",
  "data": {
    "field": "page"
  }
}
```

### 잘못된 페이지 크기 요청

- HTTP Status: `400 Bad Request`
- API Code: `4000`

`size`가 1보다 작거나 100보다 큰 경우, 또는 정수가 아닌 경우 반환합니다.

```json
{
  "code": 4000,
  "message": "잘못된 요청입니다.",
  "data": {
    "field": "size"
  }
}
```

## Swagger

애플리케이션 실행 후 다음 주소에서 동일한 명세와 호출 테스트 화면을 확인할 수 있습니다.

```text
http://localhost:8080/swagger-ui.html
```
