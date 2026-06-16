package ingprompt.patricia.events.infrastructure.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.headers.Header;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.StringSchema;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI eventsOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Events API")
                        .description("Microservice that manages Events: standalone or linked to a Parche, with participant management.")
                        .version("v0.0.1")
                        .contact(new Contact().name("PATRICIA - Events")))
                .components(new Components()
                        .addHeaders("X-User-Id", new Header()
                                .description("UUID of the authenticated user performing the action")
                                .schema(new StringSchema().format("uuid"))));
    }
}
