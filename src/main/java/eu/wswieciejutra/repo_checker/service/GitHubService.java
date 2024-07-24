package eu.wswieciejutra.repo_checker.service;

import eu.wswieciejutra.repo_checker.exception.UserNotFoundException;
import eu.wswieciejutra.repo_checker.service.dto.RepositoryDto;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import repository.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GitHubService {

    private final RestTemplate restTemplate;
    private final String githubApiUrl = "https://api.github.com";

    public GitHubService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public List<RepositoryDto> getNonForkRepositories(String username) throws UserNotFoundException {
        String url = githubApiUrl + "/users/" + username + "/repos";
        ResponseEntity<Repository[]> response = restTemplate.getForEntity(url, Repository[].class);

        if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
            throw new UserNotFoundException("User not found");
        }

        Repository[] repositories = response.getBody();
        return Arrays.stream(repositories)
                .filter(repo -> !repo.isFork())
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private RepositoryDto convertToDto(Repository repository) {
        return new RepositoryDto(repository.getName(), repository.getOwner().getLogin(), repository.isFork());
    }
}

