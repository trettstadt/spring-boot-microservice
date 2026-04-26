package de.trettstadt.microservices.springbootmicroservice.application.port.out.rooms;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration properties for the Rooms API client.
 */
@Component
@ConfigurationProperties(prefix = "rooms")
@Data
public class RoomsConfigurationProperties {

  private String basePath;
}
