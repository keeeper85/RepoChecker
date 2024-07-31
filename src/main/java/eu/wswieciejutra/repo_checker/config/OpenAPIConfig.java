package eu.wswieciejutra.repo_checker.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
class OpenAPIConfig {

    @Value("${repo-checker.openapi.local-url}")
    private String localUrl;

    @Value("${repo-checker.openapi.prod-url}")
    private String prodUrl;

    @Bean
    public OpenAPI myOpenAPI() {
        Server devServer = new Server();
        devServer.setUrl(localUrl);
        devServer.setDescription("Server URL in Development environment");

        Server prodServer = new Server();
        prodServer.setUrl(prodUrl);
        prodServer.setDescription("Server URL in Production environment");

        Contact contact = new Contact();
        contact.setEmail("raczkowski.j@hotmail.com");
        contact.setName("Jakub Raczkowski");
        contact.setUrl("https://www.wswieciejutra.eu");

        License mitLicense = new License().name("GPL License").url("https://choosealicense.com/licenses/gpl-3.0/");

        Info info = new Info()
                .title("RepoChecker API")
                .version("1.0")
                .contact(contact)
                .description("This API exposes endpoints to manage GitHub Api requests.").termsOfService("https://wswieciejutra.eu")
                .license(mitLicense);

        return new OpenAPI().info(info).servers(List.of(devServer, prodServer));
    }
}
