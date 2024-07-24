package eu.wswieciejutra.repo_checker.controller;

import eu.wswieciejutra.repo_checker.exception.UserNotFoundException;
import eu.wswieciejutra.repo_checker.service.GitHubService;
import eu.wswieciejutra.repo_checker.service.dto.RepositoryDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/github")
public class GitHubController {

    private final GitHubService gitHubService;

    public GitHubController(GitHubService gitHubService) {
        this.gitHubService = gitHubService;
    }

    @GetMapping("/{username}")
    public ResponseEntity<?> getRepositories(@PathVariable String username) {
        try {
            List<RepositoryDto> repositories = gitHubService.getNonForkRepositories(username);
            return ResponseEntity.ok(repositories);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "status", HttpStatus.NOT_FOUND.value(),
                    "message", "User not found"
            ));
        }
    }
}

