# 영문 콘텐츠 마이그레이션

## 작성된 파일

- `english_content_migration.sql`: 8개 테이블에 nullable 영문 컬럼 65개 추가.
- `english_featured_course_seed.sql`: 저장소의 홈 추천 코스 20종에 영문 제목 초안 반영.
- `english_content_translation_export.sql`: 원문이 있고 영문이 없는 필드 추출.

컬럼 추가 SQL은 `app/src/main/resources/schema.sql`과 `oisoBE.sql`에도 반영했다.
현재 local/prod의 `spring.sql.init.mode=always` 설정에서는 새 빌드 시작 시
`schema.sql`을 통해 컬럼을 추가한다. `IF NOT EXISTS`로 재실행 가능하다.
제목 번역 시드와 추출 SQL은 자동 실행 대상이 아니다.

## Git에 포함할 파일

번역 실행 환경인 `.tmp/translation-runtime/`과 그 안의 `node_modules`, 모델·캐시는
로컬 임시 파일이다. 루트 `.gitignore`의 `/.tmp/`로 제외하며 배포에 필요하지 않다.
번역 결과는 검토한 영문 문자열을 포함한 SQL로 저장하고, 컬럼 추가 SQL 및 적용 안내와
함께 커밋한다. SQL 실행 시 번역 런타임이나 모델 다운로드는 필요하지 않다.
현재 실제 영문 문자열이 들어 있는 파일은 `english_featured_course_seed.sql`이며,
`english_content_migration.sql`은 컬럼만 추가한다.

## 범위

관광지, 주변 장소, 장소 상세, 객실, 스토리, 해시태그, 메뉴, 홈 추천 코스 제목.
기존 컬럼은 원문으로 유지하고 대응하는 `_en` 컬럼을 추가한다.
객실의 text 필드에는 인원·요금 안내 문장이 들어갈 수 있어 함께 포함한다.
ID, 날짜, 숫자, URL, 코드, 사용자 메모와 닉네임은 번역하지 않는다.
코스 제목의 번역 시드·추출은 `featured_yn=true`인 코스에만 적용한다.

## 적용 순서

1. 컬럼 추가 마이그레이션 적용 또는 새 빌드 시작.
2. 홈 추천 코스 데이터가 존재하는 환경에서 제목 번역 시드 실행.
3. 추출 SQL 결과를 CSV 등으로 내보내 나머지 원문 번역.
4. 번역 반영 시 기본키와 원문 일치를 확인하고 기존 번역을 덮어쓰지 않는다.

로컬 `.tmp/translation-source.tsv`에 번역 원문 추출본이 있으며,
`.tmp/translation-unique.json`에는 중복 제거한 원문이 있다. 이 추출본이 현재 운영 DB의
전체 최신 콘텐츠와 일치하는지는 별도 확인이 필요하다. 나머지 콘텐츠의 번역 결과 SQL은
아직 작성되지 않았으며, 영문 컬럼 추가만으로 번역이 생성되지는 않는다.
추출 결과는 `table_name`, `row_id`, `field_name`, `source_ko`, `translation_en`이다.
원문이 바뀌면 번역을 재검토해야 하며, 객실처럼 삭제 후 재생성되는 데이터는
수집 과정의 영문 보존·재번역 정책도 필요하다.

## API 적용은 별도 작업

이 변경은 DB 준비 작업이며 아직 `Accept-Language`로 응답이 바뀌지는 않는다.
후속 구현에서 `en` 요청에 `COALESCE(NULLIF(btrim(name_en), ''), name)` 형태로
영문 우선·한국어 대체 조회를 적용한다. 헤더 미지정은 한국어로 처리한다.
공통 메시지·검증 오류·상태 표시 문구는 애플리케이션 메시지 리소스로 번역하며,
지도 및 Python 추천 서버 응답에는 별도의 언어 전달·응답 처리가 필요하다.

SQL은 실제 PostgreSQL에 실행 검증하지 않았다.
