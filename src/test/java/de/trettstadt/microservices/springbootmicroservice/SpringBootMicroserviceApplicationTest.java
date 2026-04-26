package de.trettstadt.microservices.springbootmicroservice;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@Tag("integration")
class SpringBootMicroserviceApplicationTest {

  @MockitoBean
  private OAuth2AuthorizedClientManager authorizedClientManager;

  @Test
  void contextLoads() {
  }
}
