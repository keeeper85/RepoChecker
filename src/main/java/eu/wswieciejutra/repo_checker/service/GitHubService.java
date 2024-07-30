package eu.wswieciejutra.repo_checker.service;

import eu.wswieciejutra.repo_checker.exception.UserNotFoundException;
import eu.wswieciejutra.repo_checker.service.dto.BranchDto;
import eu.wswieciejutra.repo_checker.service.dto.RepositoryDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import eu.wswieciejutra.repo_checker.repository.Branch;
import eu.wswieciejutra.repo_checker.repository.Repository;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GitHubService implements CodeRepositoryService{

    private final RestTemplate restTemplate;
    private final String apiUrl = Services.GITHUB.getApiUrl();

    @Autowired
    public GitHubService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public GitHubService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public List<RepositoryDto> getNonForkRepositories(String username, String token) throws UserNotFoundException {
        String url = UriComponentsBuilder.fromHttpUrl(apiUrl)
                .pathSegment("users", username, "repos")
                .toUriString();

        HttpEntity<String> entity = Factory.createHttpEntity(token);

        try {
            ResponseEntity<Repository[]> response = restTemplate.exchange(url, HttpMethod.GET, entity, Repository[].class);
            Repository[] repositories = response.getBody();

            return Arrays.stream(repositories)
                    .filter(repo -> !repo.isFork())
                    .map(repo -> Factory.convertGitHubToDto(this, repo, token))
                    .collect(Collectors.toList());
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new UserNotFoundException("User not found");
            } else {
                throw e;
            }
        }
    }

    public List<BranchDto> fetchBranchesForRepository(String owner, String repoName, String token) {
        String url = String.format("%s/repos/%s/%s/branches", apiUrl, owner, repoName);

        HttpEntity<String> entity = Factory.createHttpEntity(token);

        System.out.println("Fetching branches from URL: " + url);
        if (token != null && !token.isEmpty()) {
            System.out.println("Using token for authentication");
        } else {
            System.out.println("No token provided");
        }

        ResponseEntity<Branch[]> response;
        try {
            response = restTemplate.exchange(url, HttpMethod.GET, entity, Branch[].class);
            if (response.getBody() == null) {
                System.out.println("No branches found for repository: " + repoName);
                return Collections.emptyList();
            }
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                System.out.println("Repository not found: " + repoName);
                return Collections.emptyList();
            } else {
                throw e;
            }
        }

        Branch[] branches = response.getBody();
        System.out.println("Fetched branches: " + Arrays.toString(branches));

        return (branches != null) ? Arrays.stream(branches)
                .map(branch -> Factory.convertToBranchDto(branch))
                .collect(Collectors.toList())
                : Collections.emptyList();
    }


}
