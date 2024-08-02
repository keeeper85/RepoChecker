package eu.wswieciejutra;

import eu.wswieciejutra.controller.ApiController;
import eu.wswieciejutra.dto.RepositoryDto;
import eu.wswieciejutra.exception.UserNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ApiController.class)
public class ApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private Facade facade;

    @Test
    public void shouldReturnNonForkRepositories() throws Exception {
        // Given
        String username = "validuser";
        String token = "token";
        String service = "github";

        RepositoryDto repositoryDto1 = new RepositoryDto("repo1", "validuser", false);
        RepositoryDto repositoryDto2 = new RepositoryDto("repo2", "validuser", false);

        Mockito.when(facade.getNonForkRepositories(service, username, token))
                .thenReturn(Arrays.asList(repositoryDto1, repositoryDto2));

        // When
        ResultActions response = mockMvc.perform(get("/api/github/{username}", username)
                .param("token", token)
                .param("service", service)
                .contentType(MediaType.APPLICATION_JSON));

        // Then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("repo1"))
                .andExpect(jsonPath("$[1].name").value("repo2"));
    }

    @Test
    public void shouldReturn404WhenUserNotFound() throws Exception {
        // Given
        String username = "invaliduser";
        String token = "token";
        String service = "github";

        Mockito.when(facade.getNonForkRepositories(service, username, token))
                .thenThrow(new UserNotFoundException("User not found"));

        // When
        ResultActions response = mockMvc.perform(get("/api/github/{username}", username)
                .param("token", token)
                .param("service", service)
                .contentType(MediaType.APPLICATION_JSON));

        // Then
        response.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("User not found"));
    }

    @Test
    public void shouldReturn500WhenUnexpectedErrorOccurs() throws Exception {
        // Given
        String username = "validuser";
        String token = "token";
        String service = "github";

        Mockito.when(facade.getNonForkRepositories(service, username, token))
                .thenThrow(new RuntimeException("Unexpected error"));

        // When
        ResultActions response = mockMvc.perform(get("/api/github/{username}", username)
                .param("token", token)
                .param("service", service)
                .contentType(MediaType.APPLICATION_JSON));

        // Then
        response.andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value("An unexpected error occurred"));
    }
}
