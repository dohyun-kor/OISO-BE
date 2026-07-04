-- public."AccommodationDetail" definition

-- Drop table

-- DROP TABLE public."AccommodationDetail";

CREATE TABLE public."AccommodationDetail"
(
    temp_room_id                int8 NOT NULL,
    spot_id                     int8 NOT NULL,
    room_code                   text NULL,
    room_title                  text NULL,
    room_size_pyeong            text NULL,
    room_size_square_meter      text NULL,
    room_count                  text NULL,
    base_count                  text NULL,
    max_count                   text NULL,
    offseason_weekday_min_fee   text NULL,
    offseason_weekend_min_fee   text NULL,
    peak_season_weekday_min_fee text NULL,
    peak_season_weekend_min_fee text NULL,
    room_intro                  text NULL,
    has_bath_facility           text NULL,
    has_bath                    text NULL,
    has_home_theater            text NULL,
    has_air_condition           text NULL,
    has_tv                      text NULL,
    has_pc                      text NULL,
    has_cable                   text NULL,
    has_internet                text NULL,
    has_refrigerator            text NULL,
    has_toiletries              text NULL,
    has_sofa                    text NULL,
    has_cook                    text NULL,
    has_table                   text NULL,
    has_hairdryer               text NULL,
    CONSTRAINT "PK_ACCOMMODATIONDETAIL" PRIMARY KEY (temp_room_id, spot_id)
);


-- public."HashTag" definition

-- Drop table

-- DROP TABLE public."HashTag";

CREATE TABLE public."HashTag"
(
    hashtag_id int8 NOT NULL,
    "name"     varchar NULL,
    CONSTRAINT "PK_HASHTAG" PRIMARY KEY (hashtag_id)
);


-- public."Memo" definition

-- Drop table

-- DROP TABLE public."Memo";

CREATE TABLE public."Memo"
(
    history_id     int8 NOT NULL,
    user_id        int8 NOT NULL,
    tour_course_id int8 NOT NULL,
    user_id2       int8 NOT NULL,
    review         varchar NULL,
    rating         int4 NULL,
    progress       float8 NULL,
    img            varchar NULL,
    CONSTRAINT "PK_MEMO" PRIMARY KEY (history_id, user_id, tour_course_id, user_id2)
);


-- public."Mission" definition

-- Drop table

-- DROP TABLE public."Mission";

CREATE TABLE public."Mission"
(
    mission_id int8 NOT NULL,
    spot_id    int8 NOT NULL,
    user_id    int8 NOT NULL,
    visit      bool NULL,
    qr_code    bool NULL,
    story_card bool NULL,
    "percent"  float8 NULL,
    CONSTRAINT "PK_MISSION" PRIMARY KEY (mission_id, spot_id, user_id)
);


-- public."NearbyPlaceDetail" definition

-- Drop table

-- DROP TABLE public."NearbyPlaceDetail";

CREATE TABLE public."NearbyPlaceDetail"
(
    tour_spot_nearby_course_id int8 NOT NULL,
    spot_id                    int8 NOT NULL,
    fee_info                   varchar(255) NULL,
    discount_info              varchar(255) NULL,
    credit_card_info           varchar(255) NULL,
    open_time                  varchar(255) NULL,
    rest_date                  varchar(255) NULL,
    reservation_info           varchar(255) NULL,
    inquiry_info               varchar(255) NULL,
    parking_info               varchar(255) NULL,
    restroom_info              varchar(255) NULL,
    baby_carriage_info         varchar(255) NULL,
    pet_info                   varchar(255) NULL,
    smoking_info               varchar(255) NULL,
    first_menu                 varchar(255) NULL,
    treat_menu                 varchar(255) NULL,
    packing_info               varchar(255) NULL,
    kids_facility_info         varchar(255) NULL,
    experience_info            varchar(255) NULL,
    age_limit_info             varchar(255) NULL,
    scale_info                 varchar(255) NULL,
    use_season_info            varchar(255) NULL,
    spend_time_info            varchar(255) NULL,
    CONSTRAINT "PK_NEARBYPLACEDETAIL" PRIMARY KEY (tour_spot_nearby_course_id, spot_id)
);


-- public."StoryCard_HashTag" definition

-- Drop table

-- DROP TABLE public."StoryCard_HashTag";

CREATE TABLE public."StoryCard_HashTag"
(
    hashtag_id int8 NOT NULL,
    story_id   int8 NOT NULL,
    spot_id    int8 NOT NULL,
    CONSTRAINT "PK_STORYCARD_HASHTAG" PRIMARY KEY (hashtag_id, story_id, spot_id)
);


-- public."TourCourse" definition

-- Drop table

-- DROP TABLE public."TourCourse";

CREATE TABLE public."TourCourse"
(
    tour_course_id int8 NOT NULL,
    user_id        int8 NOT NULL,
    title          varchar NULL,
    CONSTRAINT "PK_TOURCOURSE" PRIMARY KEY (tour_course_id, user_id)
);


-- public."TourCourseItem" definition

-- Drop table

-- DROP TABLE public."TourCourseItem";

CREATE TABLE public."TourCourseItem"
(
    item_id                     int8 NOT NULL,
    tour_spot_nearby_course_id2 int8 NOT NULL,
    spot_id                     int8 NOT NULL,
    tour_course_id              int8 NULL,
    item_order                  int8 NULL,
    CONSTRAINT "PK_TOURCOURSEITEM" PRIMARY KEY (item_id, tour_spot_nearby_course_id2, spot_id)
);


-- public."TourSpot" definition

-- Drop table

-- DROP TABLE public."TourSpot";

CREATE TABLE public."TourSpot"
(
    spot_id         int8 NOT NULL,
    "name"          varchar NULL,
    img             varchar NULL,
    "content"       varchar NULL,
    address         varchar NULL,
    can_parking     bool NULL,
    can_pet         bool NULL,
    "like"          bool NULL,
    crowd_level     int4 NULL,
    overview        varchar NULL,
    homepage        varchar NULL,
    map_x           float8 NULL,
    map_y           float8 NULL,
    content_id      int8 NULL,
    content_type_id int8 NULL,
    tel             varchar NULL,
    CONSTRAINT "PK_TOURSPOT" PRIMARY KEY (spot_id)
);
CREATE UNIQUE INDEX ux_tour_spot_content_id ON public."TourSpot" USING btree (content_id) WHERE (content_id IS NOT NULL);


-- public."TourSpotNearbyPlace" definition

-- Drop table

-- DROP TABLE public."TourSpotNearbyPlace";

CREATE TABLE public."TourSpotNearbyPlace"
(
    tour_spot_nearby_course_id int8 NOT NULL,
    spot_id                    int8 NOT NULL,
    "name"                     varchar NULL,
    category                   varchar(30) NULL,
    mood                       varchar(30) NULL,
    address                    varchar NULL,
    hompage_url                varchar NULL,
    content_id                 int8 NULL,
    content_type_id            int8 NULL,
    map_x                      float8 NULL,
    map_y                      float8 NULL,
    CONSTRAINT "PK_TOURSPOTNEARBYPLACE" PRIMARY KEY (tour_spot_nearby_course_id, spot_id)
);


-- public."TourStory" definition

-- Drop table

-- DROP TABLE public."TourStory";

CREATE TABLE public."TourStory"
(
    story_id  int8 NOT NULL,
    spot_id   int8 NOT NULL,
    title     varchar NULL,
    sub_title varchar NULL,
    summary   varchar NULL,
    story     varchar NULL,
    img       varchar NULL,
    tip       varchar NULL,
    "comment" varchar NULL,
    CONSTRAINT "PK_TOURSTORY" PRIMARY KEY (story_id, spot_id)
);


-- public."User" definition

-- Drop table

-- DROP TABLE public."User";

CREATE TABLE public."User"
(
    user_id                int8 NOT NULL,
    has_travel_plan        bool NULL,
    travel_time            int8 NULL,
    travel_companion       int8 NULL,
    preferred_travel_theme int8 NULL,
    transportation_mode    int8 NULL,
    "language"             int8 NULL,
    nickname               varchar NULL,
    "Field"                varchar(255) NULL,
    "Field2"               varchar(255) NULL,
    CONSTRAINT "PK_USER" PRIMARY KEY (user_id)
);


-- public.tour_ingest_execution definition

-- Drop table

-- DROP TABLE public.tour_ingest_execution;

CREATE TABLE public.tour_ingest_execution
(
    id              bigserial   NOT NULL,
    status          varchar(20) NOT NULL,
    started_at      timestamp   NOT NULL,
    finished_at     timestamp NULL,
    processed_count int4        NOT NULL,
    failure_message text NULL,
    CONSTRAINT tour_ingest_execution_pkey PRIMARY KEY (id)
);