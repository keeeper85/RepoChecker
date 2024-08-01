package eu.wswieciejutra.repo_checker.controller;

import eu.wswieciejutra.repo_checker.exception.ApiLimitReachedException;
import eu.wswieciejutra.repo_checker.exception.UserNotFoundException;
import eu.wswieciejutra.repo_checker.logging.LoggerUtility;
import eu.wswieciejutra.repo_checker.service.Facade;
import eu.wswieciejutra.repo_checker.service.dto.RepositoryDto;
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

    public ApiController(Facade facade) {
        this.facade = facade;
    }

    @Operation(description = "Get the GitHub user's repositories as JSON objects")
    @GetMapping("/{username}")
    public ResponseEntity<List<RepositoryDto>> getRepositories(@PathVariable String username,
                                                               @RequestParam(required = false) String token) throws UserNotFoundException, ApiLimitReachedException {
        LoggerUtility.LOGGER.info("Api used for user: {}", username);
        List<RepositoryDto> repositories = facade.getNonForkRepositories("GITHUB", username, token);
        return ResponseEntity.ok(repositories);
    }
}
