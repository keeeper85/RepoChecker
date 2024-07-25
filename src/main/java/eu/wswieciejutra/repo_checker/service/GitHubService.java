package eu.wswieciejutra.repo_checker.service;

import eu.wswieciejutra.repo_checker.exception.UserNotFoundException;
import eu.wswieciejutra.repo_checker.service.dto.BranchDto;
import eu.wswieciejutra.repo_checker.service.dto.RepositoryDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import repository.Branch;
import repository.Repository;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GitHubService {

    private final RestTemplate restTemplate;
    private final String githubApiUrl = "https://api.github.com";

    @Autowired
    public GitHubService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public GitHubService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<RepositoryDto> getNonForkRepositories(String username, String token) throws UserNotFoundException {
        String url = UriComponentsBuilder.fromHttpUrl(githubApiUrl)
                .pathSegment("users", username, "repos")
                .toUriString();

        HttpEntity<String> entity = createHttpEntity(token);

        try {
            ResponseEntity<Repository[]> response = restTemplate.exchange(url, HttpMethod.GET, entity, Repository[].class);
            Repository[] repositories = response.getBody();

            return Arrays.stream(repositories)
                    .filter(repo -> !repo.isFork())
                    .map(repo -> convertToDto(repo, token))
                    .collect(Collectors.toList());
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new UserNotFoundException("User not found");
            } else {
                throw e;
            }
        }
    }

    private RepositoryDto convertToDto(Repository repository, String token) {
        RepositoryDto dto = new RepositoryDto();
        dto.setName(repository.getName());
        dto.setOwner(repository.getOwner().getLogin());
        dto.setFork(repository.isFork());
        List<BranchDto> branches = fetchBranchesForRepository(repository.getOwner().getLogin(), repository.getName(), token);
        dto.setBranches(branches);

        return dto;
    }

    private List<BranchDto> fetchBranchesForRepository(String owner, String repoName, String token) {
        String url = String.format("%s/repos/%s/%s/branches", githubApiUrl, owner, repoName);

        HttpEntity<String> entity = createHttpEntity(token);

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
                .map(this::convertToBranchDto)
                .collect(Collectors.toList())
                : Collections.emptyList();
    }

    private BranchDto convertToBranchDto(Branch branch) {
        BranchDto dto = new BranchDto();
        dto.setName(branch.getName());
        dto.setLastCommitSha(branch.getCommit().getSha());
        return dto;
    }

    private HttpEntity<String> createHttpEntity(String token) {
        HttpHeaders headers = new HttpHeaders();
        if (token != null && !token.isEmpty()) {
            headers.set("Authorization", "Bearer " + token);
        }
        return new HttpEntity<>(headers);
    }
}
