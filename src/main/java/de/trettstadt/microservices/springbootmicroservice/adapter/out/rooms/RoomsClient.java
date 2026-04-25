package de.trettstadt.microservices.springbootmicroservice.adapter.out.rooms;

import de.trettstadt.microservices.springbootmicroservice.adapter.out.rooms.api.ApiClient;
import de.trettstadt.microservices.springbootmicroservice.adapter.out.rooms.api.RoomApi;
import de.trettstadt.microservices.springbootmicroservice.adapter.out.rooms.api.model.RoomList;
import de.trettstadt.microservices.springbootmicroservice.application.port.out.rooms.RoomsConfigurationProperties;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.client.OAuth2ClientHttpRequestInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class RoomsClient {
    private final RoomApi roomApi;

    public RoomsClient(OAuth2AuthorizedClientManager oAuth2AuthorizedClientManager, RoomsConfigurationProperties roomsConfigurationProperties) {
        OAuth2ClientHttpRequestInterceptor requestInterceptor = new OAuth2ClientHttpRequestInterceptor(oAuth2AuthorizedClientManager);
        requestInterceptor.setClientRegistrationIdResolver(request -> "rooms");
        RestClient restClient = RestClient.builder()
                .requestInterceptor(requestInterceptor)
                .build();
        ApiClient apiClient = new ApiClient(restClient);
        apiClient.setBasePath(roomsConfigurationProperties.getBasePath());
        this.roomApi = new RoomApi(apiClient);
    }

    public RoomList getRooms() {
        return roomApi.getRooms();
    }
}
