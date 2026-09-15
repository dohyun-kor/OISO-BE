# 식당·숙소 개인 메모

배포 전에 `nearby_place_memo_migration.sql`을 PostgreSQL에 적용합니다.
기존 관광지 메모를 유지하며 식당·숙소 메모를 추가합니다.

모든 요청은 로그인이 필요합니다. 장소 ID는 주변 장소 조회의 `nearbyPlaceId`이며,
부모 관광지의 `spotId`가 아닙니다. 사용자마다 장소별 메모 하나를 저장합니다.

| 메서드 | 경로 | 기능 |
| --- | --- | --- |
| PUT | `/api/v1/memos/nearby-places/{nearbyPlaceId}` | 작성 또는 덮어쓰기 |
| GET | `/api/v1/memos/nearby-places/{nearbyPlaceId}` | 본인 메모 조회 |
| DELETE | `/api/v1/memos/nearby-places/{nearbyPlaceId}` | 본인 메모 삭제 |

PUT 요청 예시:

```json
{"content":"다음에 다시 방문하기", "imageUrl":"https://example.com/memo.jpg"}
```

내용은 공백을 제외한 필수 입력이며 최대 1,000자입니다. 이미지 URL은 선택 입력이며
최대 2,048자입니다. 이미지 파일 업로드 API는 아닙니다.

응답의 `data`는 `nearbyPlaceId`와 `memo`를 포함합니다. `memo`에는 기존 관광지
메모와 동일하게 `memoId`, `content`, `imageUrl`, `createdAt`, `updatedAt`이 있습니다.
메모 미작성 또는 삭제 후에는 `memo`가 null입니다. 없는 장소는 404입니다.

## 내 메모 목록

`GET /api/v1/memos?page=0&size=20`은 관광지·식당·숙소 메모를 함께 반환합니다.
최근 수정 순으로 정렬하며, 전체 건수도 세 종류를 합산합니다.

- 관광지: `category=TOUR_SPOT`, `spotId` 존재, `nearbyPlaceId=null`
- 식당·숙소: `category=RESTAURANT` 또는 `ACCOMMODATION`, `spotId=null`, `nearbyPlaceId` 존재
- 기존 `spotName`, `spotImage` 필드는 해당 장소의 이름과 이미지를 제공합니다.

프런트엔드는 `category`와 대상 ID를 사용해 상세 메모 API를 선택해야 합니다.
