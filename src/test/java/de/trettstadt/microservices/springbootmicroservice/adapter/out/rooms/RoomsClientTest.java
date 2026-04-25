package de.trettstadt.microservices.springbootmicroservice.adapter.out.rooms;

import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigInteger;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@SpringBootTest
class RoomsClientTest {

    @RegisterExtension
    static WireMockExtension wireMockServer = WireMockExtension.newInstance()
            .options(WireMockConfiguration.options().dynamicPort())
            .build();
    @Autowired
    private RoomsClient roomsClient;

    @MockitoBean
    private OAuth2AuthorizedClientManager authorizedClientManager;

    @DynamicPropertySource
    static void dynamicProperties(DynamicPropertyRegistry dynamicPropertyRegistry) {
        dynamicPropertyRegistry.add("rooms.basePath", () -> wireMockServer.baseUrl());
    }

    @Test
    void shouldReturnRoomsFromApi() {
        wireMockServer.stubFor(get(urlEqualTo("/rooms"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody("""
                                {
                                  "data": [
                                    {
                                      "id": 1,
                                      "description": "Test Room"
                                    }
                                  ]
                                }
                                """)
                ));

        var result = roomsClient.getRooms();

        assertThat(result.getData()).extracting("id", "description")
                .containsExactly(tuple(BigInteger.ONE, "Test Room"));
    }

    @Test
    void shouldReturnEmptyListWhenNoRooms() {
        wireMockServer.stubFor(get(urlEqualTo("/rooms"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody("""
                                {
                                  "data": []
                                }
                                """)
                ));

        var result = roomsClient.getRooms();

        assertThat(result.getData()).isEmpty();
    }
}