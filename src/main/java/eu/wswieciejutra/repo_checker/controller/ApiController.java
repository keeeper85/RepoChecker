package eu.wswieciejutra.repo_checker.controller;

import eu.wswieciejutra.repo_checker.exception.UserNotFoundException;
import eu.wswieciejutra.repo_checker.service.Facade;
import eu.wswieciejutra.repo_checker.service.Services;
import eu.wswieciejutra.repo_checker.service.dto.RepositoryDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
    public ResponseEntity<?> getRepositories(@PathVariable String username,
                                             @RequestParam(required = false) String token) {
        try {
            List<RepositoryDto> repositories = facade.getNonForkRepositories("GITHUB", username, token);
            return ResponseEntity.ok(repositories);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "status", HttpStatus.NOT_FOUND.value(),
                    "message", "User not found"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "message", "An unexpected error occurred" + e.getMessage()
            ));
        }
    }
}
