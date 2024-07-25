package eu.wswieciejutra.repo_checker.controller;

import eu.wswieciejutra.repo_checker.exception.UserNotFoundException;
import eu.wswieciejutra.repo_checker.service.GitHubService;
import eu.wswieciejutra.repo_checker.service.dto.RepositoryDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/github")
public class ApiController {

    private static final Logger logger = LoggerFactory.getLogger(ApiController.class);
    private final GitHubService gitHubService;

    public ApiController(GitHubService gitHubService) {
        this.gitHubService = gitHubService;
    }

    @GetMapping("/{username}")
    public ResponseEntity<?> getRepositories(@PathVariable String username, @RequestParam(required = false) String token) {
        try {
            logger.info("Fetching repositories for user: {}", username);
            List<RepositoryDto> repositories = gitHubService.getNonForkRepositories(username, token);
            return ResponseEntity.ok(repositories);
        } catch (UserNotFoundException e) {
            logger.error("User not found: {}", username, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "status", HttpStatus.NOT_FOUND.value(),
                    "message", "User not found"
            ));
        } catch (Exception e) {
            logger.error("An unexpected error occurred", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "message", "An unexpected error occurred"
            ));
        }
    }
}
