package eu.wswieciejutra.controller;

import eu.wswieciejutra.Facade;
import eu.wswieciejutra.LoggerUtility;
import eu.wswieciejutra.dto.RepositoryDto;
import eu.wswieciejutra.exception.ApiLimitReachedException;
import eu.wswieciejutra.exception.UserNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/github")
@Tag(name = "Api Controller", description = "Search for GitHub users in text mode")
public class ApiController {

    private final Facade facade;

    ApiController(Facade facade) {
        this.facade = facade;
    }

    @Operation(description = "Get the GitHub user's repositories as JSON objects")
    @GetMapping("/{username}")
    ResponseEntity<List<RepositoryDto>> getRepositories(@PathVariable String username,
                                                               @RequestParam(required = false) String token) throws UserNotFoundException, ApiLimitReachedException {
        LoggerUtility.LOGGER.info("Api used for user: {}", username);
        List<RepositoryDto> repositories = facade.getNonForkRepositories("GITHUB", username, token);
        return ResponseEntity.ok(repositories);
    }
}
