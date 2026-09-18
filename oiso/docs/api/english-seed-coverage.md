# Repository-backed English content seed

These translations are based on Korean copy checked into this repository. They are not a translation of every row in the live database. Story names and six major cards were translated manually; the remaining long-form cards were machine-assisted and need editorial review before production use.

## Apply manually, in order

1. Apply `english_content_migration.sql` or deploy the version of `app/src/main/resources/schema.sql` that creates the `_en` columns.
2. Apply the existing `english_featured_course_seed.sql` for the 20 repository-provided featured course titles, if those course rows exist.
3. Apply `english_story_content_seed.sql` after `story_image_migration.sql` has populated story cards.
4. Apply `english_story_names_seed.sql` for all checked-in story-card titles and subtitles.
5. Apply `english_story_longform_seed.sql` for the remaining story-card body fields, after translation review.
6. Apply `english_content_reuse_seed.sql` to fill DB-only provider fields only where their Korean source exactly matches an unambiguous translated story value.
7. Apply `english_menu_content_seed.sql` after `restaurant_menu_seed.sql` has populated menu rows.
8. Apply `english_menu_content_seed_additional.sql` and `english_menu_content_seed_brand_names.sql` for the remaining menu labels.

The story and menu seed files are **not** executed automatically by Spring. They are repeatable and fill only blank English columns. The Korean source must match exactly; story paragraph matching ignores CRLF/LF differences only. They do not touch user memos or ordinary user-created course titles.

## Covered by the new files

- `english_story_content_seed.sql`: 19 distinct `TourStory.story_title` phrases used by all 295 story cards in the repository seed; 11 reusable subtitle suffixes, applied when a reviewed `title_en` is present; 21 reviewed place names in `TourStory.title_en` and matching `TourSpot.name_en`; and all five long-form text fields (`summary`, `intro`, `story`, `tip`, `did_you_know`) for six selected places (spot IDs 5, 7, 29, 35, 36, 41). The exact source text of all 30 long-form fields was verified against `story_image_migration.sql`.
- `english_story_names_seed.sql`: all 295 checked-in `TourStory.title` strings, matching `TourSpot.name` strings, and all 295 card subtitles. Of the subtitles, 294 match the shared suffix rules and the 2025 Korea Craft Exhibition subtitle needs an exact special case. Names are descriptive English translations or transliterations, not verified official venue spellings.
- `english_story_longform_seed.sql`: the remaining 289 cards × five fields = 1,445 translated fields. Together with the 30 manually translated fields in `english_story_content_seed.sql`, this covers all 295 cards' `summary`, `intro`, `story`, `tip`, and `did_you_know` source fields. Only provider-authored source copy was sent to a translation service; no user memos or user-created course titles were sent. Four fields containing untranslated Korean were translated again in smaller units. The generated SQL verifies the spot ID and exact Korean source before filling blank English fields. This is complete *field coverage*, not a guarantee that every venue name or historical detail is editorially perfect.
- `english_content_reuse_seed.sql`: exact-source, unambiguous reuse for matching `TourSpot.name`, `TourSpot.content`, `TourSpot.overview`, `TourSpotNearbyPlace.name`, and `TourSpotNearbyPlace.overview`. The number of matching live DB rows is unknown until the SQL is applied.
- Three `english_menu_content_seed*.sql` files: all 286 distinct literal menu names found in `restaurant_menu_seed.sql` (128 + 148 + 10, with no overlap). They fill `NearbyPlaceMenu.menu_name_en` and exact matches in `NearbyPlaceDetail.first_menu_en` / `treat_menu_en`. The count includes seed rows whose restaurant ID is written as either a number or a quoted number. These are human-proposed translations of the checked-in Korean labels, not official English menu labels supplied by each venue. The last 10 venue-specific labels are transliterated rather than having their ingredients guessed.
- `english_featured_course_seed.sql` already contains English titles for 20 repository-provided featured courses; no new translation was needed for those titles.

## Remaining work, separated by input needed

- Names, subtitles, all five long-form story fields, and literal menu names are covered without an additional database export. For venue-specific names, official English brand spellings can optionally replace these transliterations. Two local offline translation models produced serious mistranslations on short Korean source phrases; their output was not used in migration SQL.
- Current `TourSpot` descriptions and addresses, festival rows not present in the 295-card seed, `TourSpotNearbyPlace` details, room information, hashtags, dynamically imported menu names, and other rows populated directly in the database are not present as a complete snapshot in this repository. After applying the seeds above, export the UTF-8 result of `english_content_translation_export.sql` from the target database. It returns `table_name`, `row_id`, `field_name`, `source_ko`, and `translation_en`, and excludes user `Memo` content and non-featured course titles. This export is the only *required new source data* for translating remaining live DB fields.
- The checked-in `oiso/translations/en/batch-*.json` files contain some English phrases but are keyed by ordinal index, without the live row ID and exact Korean source alongside each phrase. They cannot safely update live rows on their own; the database export above is still needed.
- A database operator must apply the SQL and verify row counts and English API responses. No live database was changed while preparing these files.

For future batches, match row ID **and current Korean source text** and update only empty `_en` fields. Recheck translations if upstream Korean copy changes.
