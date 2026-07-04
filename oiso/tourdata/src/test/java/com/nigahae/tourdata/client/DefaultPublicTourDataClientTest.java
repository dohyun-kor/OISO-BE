package com.nigahae.tourdata.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nigahae.tourdata.config.TourDataProperties;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class DefaultPublicTourDataClientTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void fetchAllTourContentsReadsItems() {
        TourDataProperties properties = properties();
        RestClient.Builder builder = RestClient.builder().baseUrl(properties.getApi().getBaseUrl());
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();

        server.expect(requestTo(containsString("/areaBasedList2")))
                .andExpect(requestTo(containsString("arrange=C")))
                .andExpect(requestTo(containsString("lDongRegnCd=47")))
                .andExpect(requestTo(containsString("lDongSignguCd=130")))
                .andRespond(withSuccess("""
                        {
                          "response": {
                            "header": {"resultCode": "0000", "resultMsg": "OK"},
                            "body": {
                              "totalCount": 1,
                              "items": {
                                "item": {
                                  "contentid": "100",
                                  "contenttypeid": "12",
                                  "title": "Test Place",
                                  "addr1": "Seoul",
                                  "addr2": "Jongno",
                                  "mapx": "127.0",
                                  "mapy": "37.5",
                                  "firstimage": "",
                                  "firstimage2": "https://example.com/a.jpg",
                                  "lDongRegnCd": "47",
                                  "lDongSignguCd": "130"
                                }
                              }
                            }
                          }
                        }
                        """, MediaType.APPLICATION_JSON));

        DefaultPublicTourDataClient client = new DefaultPublicTourDataClient(builder.build(), properties, objectMapper);

        List<TourContentPayload> results = client.fetchAllTourContents();

        assertThat(results).hasSize(1);
        assertThat(results.get(0).contentId()).isEqualTo("100");
        assertThat(results.get(0).title()).isEqualTo("Test Place");
        assertThat(results.get(0).address()).isEqualTo("Seoul Jongno");
        assertThat(results.get(0).firstImage()).isEqualTo("https://example.com/a.jpg");
        server.verify();
    }

    @Test
    void fetchAllTourContentsThrowsOnApiError() {
        TourDataProperties properties = properties();
        RestClient.Builder builder = RestClient.builder().baseUrl(properties.getApi().getBaseUrl());
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();

        server.expect(requestTo(containsString("/areaBasedList2")))
                .andRespond(withSuccess("""
                        {
                          "response": {
                            "header": {"resultCode": "99", "resultMsg": "ERROR"},
                            "body": {}
                          }
                        }
                        """, MediaType.APPLICATION_JSON));

        DefaultPublicTourDataClient client = new DefaultPublicTourDataClient(builder.build(), properties, objectMapper);

        assertThatThrownBy(client::fetchAllTourContents)
                .isInstanceOf(TourApiException.class)
                .hasMessageContaining("99");
        server.verify();
    }

    @Test
    void fetchAccommodationRoomsReadsArrayItems() {
        TourDataProperties properties = properties();
        RestClient.Builder builder = RestClient.builder().baseUrl(properties.getApi().getBaseUrl());
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();

        server.expect(requestTo(containsString("/detailInfo2")))
                .andExpect(requestTo(containsString("contentId=2637847")))
                .andExpect(requestTo(containsString("contentTypeId=32")))
                .andRespond(withSuccess("""
                        {
                          "response": {
                            "header": {"resultCode": "0000", "resultMsg": "OK"},
                            "body": {
                              "totalCount": 2,
                              "items": {
                                "item": [
                                  {
                                    "roomcode": "",
                                    "roomtitle": "Room A",
                                    "roomsize1": "10",
                                    "roomsize2": "33",
                                    "roomcount": "18",
                                    "roombasecount": "2",
                                    "roommaxcount": "4",
                                    "roomoffseasonminfee1": "60000",
                                    "roomoffseasonminfee2": "80000",
                                    "roompeakseasonminfee1": "90000",
                                    "roompeakseasonminfee2": "100000",
                                    "roomintro": "Intro",
                                    "roombathfacility": "Y",
                                    "roomaircondition": "Y",
                                    "roomtv": "Y",
                                    "roominternet": "Y",
                                    "roomrefrigerator": "Y",
                                    "roomtoiletries": "Y",
                                    "roomtable": "Y",
                                    "roomhairdryer": "Y"
                                  },
                                  {
                                    "roomcode": "50703",
                                    "roomtitle": "Room B"
                                  }
                                ]
                              }
                            }
                          }
                        }
                        """, MediaType.APPLICATION_JSON));

        DefaultPublicTourDataClient client = new DefaultPublicTourDataClient(builder.build(), properties, objectMapper);

        List<AccommodationRoomPayload> results = client.fetchAccommodationRooms(2637847L, 32L);

        assertThat(results).hasSize(2);
        assertThat(results.get(0).roomCode()).isNull();
        assertThat(results.get(0).roomTitle()).isEqualTo("Room A");
        assertThat(results.get(0).roomSizePyeong()).isEqualTo("10");
        assertThat(results.get(0).roomSizeSquareMeter()).isEqualTo("33");
        assertThat(results.get(1).roomCode()).isEqualTo("50703");
        server.verify();
    }

    @Test
    void fetchAccommodationRoomsReadsSingleItem() {
        TourDataProperties properties = properties();
        RestClient.Builder builder = RestClient.builder().baseUrl(properties.getApi().getBaseUrl());
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();

        server.expect(requestTo(containsString("/detailInfo2")))
                .andRespond(withSuccess("""
                        {
                          "response": {
                            "header": {"resultCode": "0000", "resultMsg": "OK"},
                            "body": {
                              "totalCount": 1,
                              "items": {
                                "item": {
                                  "roomcode": "50704",
                                  "roomtitle": "Single Room"
                                }
                              }
                            }
                          }
                        }
                        """, MediaType.APPLICATION_JSON));

        DefaultPublicTourDataClient client = new DefaultPublicTourDataClient(builder.build(), properties, objectMapper);

        List<AccommodationRoomPayload> results = client.fetchAccommodationRooms(2637847L, 32L);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).roomCode()).isEqualTo("50704");
        assertThat(results.get(0).roomTitle()).isEqualTo("Single Room");
        server.verify();
    }

    @Test
    void fetchAccommodationRoomsReadsMultiplePages() {
        TourDataProperties properties = properties();
        properties.getApi().setPageSize(1);
        RestClient.Builder builder = RestClient.builder().baseUrl(properties.getApi().getBaseUrl());
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();

        server.expect(requestTo(containsString("pageNo=1")))
                .andRespond(withSuccess("""
                        {
                          "response": {
                            "header": {"resultCode": "0000", "resultMsg": "OK"},
                            "body": {
                              "totalCount": 2,
                              "items": {"item": {"roomcode": "A", "roomtitle": "Room A"}}
                            }
                          }
                        }
                        """, MediaType.APPLICATION_JSON));
        server.expect(requestTo(containsString("pageNo=2")))
                .andRespond(withSuccess("""
                        {
                          "response": {
                            "header": {"resultCode": "0000", "resultMsg": "OK"},
                            "body": {
                              "totalCount": 2,
                              "items": {"item": {"roomcode": "B", "roomtitle": "Room B"}}
                            }
                          }
                        }
                        """, MediaType.APPLICATION_JSON));

        DefaultPublicTourDataClient client = new DefaultPublicTourDataClient(builder.build(), properties, objectMapper);

        List<AccommodationRoomPayload> results = client.fetchAccommodationRooms(2637847L, 32L);

        assertThat(results).extracting(AccommodationRoomPayload::roomCode).containsExactly("A", "B");
        server.verify();
    }

    @Test
    void fetchAccommodationRoomsThrowsOnApiError() {
        TourDataProperties properties = properties();
        RestClient.Builder builder = RestClient.builder().baseUrl(properties.getApi().getBaseUrl());
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();

        server.expect(requestTo(containsString("/detailInfo2")))
                .andRespond(withSuccess("""
                        {
                          "response": {
                            "header": {"resultCode": "99", "resultMsg": "ERROR"},
                            "body": {}
                          }
                        }
                        """, MediaType.APPLICATION_JSON));

        DefaultPublicTourDataClient client = new DefaultPublicTourDataClient(builder.build(), properties, objectMapper);

        assertThatThrownBy(() -> client.fetchAccommodationRooms(2637847L, 32L))
                .isInstanceOf(TourApiException.class)
                .hasMessageContaining("99");
        server.verify();
    }

    @Test
    void fetchAccommodationRoomsPropagatesHttpError() {
        TourDataProperties properties = properties();
        RestClient.Builder builder = RestClient.builder().baseUrl(properties.getApi().getBaseUrl());
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();

        server.expect(requestTo(containsString("/detailInfo2")))
                .andRespond(withServerError());

        DefaultPublicTourDataClient client = new DefaultPublicTourDataClient(builder.build(), properties, objectMapper);

        assertThatThrownBy(() -> client.fetchAccommodationRooms(2637847L, 32L))
                .isInstanceOf(RuntimeException.class);
        server.verify();
    }

    private TourDataProperties properties() {
        TourDataProperties properties = new TourDataProperties();
        properties.getApi().setBaseUrl("http://example.com");
        properties.getApi().setOperationPath("/areaBasedList2");
        properties.getApi().setDetailInfoOperationPath("/detailInfo2");
        properties.getApi().setServiceKey("secret");
        properties.getApi().setPageSize(10);
        properties.getApi().setContentTypeIds(List.of(""));
        return properties;
    }
}
