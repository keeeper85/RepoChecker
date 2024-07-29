package eu.wswieciejutra.repo_checker.controller;

import eu.wswieciejutra.repo_checker.exception.UserNotFoundException;
import eu.wswieciejutra.repo_checker.service.Facade;
import eu.wswieciejutra.repo_checker.service.dto.RepositoryDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/github")
public class ApiController {

    private final Facade facade;

    public ApiController(Facade facade) {
        this.facade = facade;
    }

    @GetMapping("/{username}")
    public ResponseEntity<?> getRepositories(@PathVariable String username,
                                             @RequestParam(required = false) String token,
                                             @RequestParam(name = "service", required = false) String service) {
        try {
            List<RepositoryDto> repositories = facade.getNonForkRepositories(service, username, token);
            return ResponseEntity.ok(repositories);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "status", HttpStatus.NOT_FOUND.value(),
                    "message", "User not found"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "message", "An unexpected error occurred"
            ));
        }
    }
}
