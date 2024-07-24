package eu.wswieciejutra.repo_checker.service;

import eu.wswieciejutra.repo_checker.exception.UserNotFoundException;
import eu.wswieciejutra.repo_checker.service.dto.BranchDto;
import eu.wswieciejutra.repo_checker.service.dto.RepositoryDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    public List<RepositoryDto> getNonForkRepositories(String username) throws UserNotFoundException {
        String url = UriComponentsBuilder.fromHttpUrl(githubApiUrl)
                .pathSegment("users", username, "repos")
                .toUriString();
        try {
            ResponseEntity<Repository[]> response = restTemplate.getForEntity(url, Repository[].class);
            Repository[] repositories = response.getBody();

            return Arrays.stream(repositories)
                    .filter(repo -> !repo.isFork())
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new UserNotFoundException("User not found");
            } else {
                throw e;
            }
        }
    }

    private RepositoryDto convertToDto(Repository repository) {
        RepositoryDto dto = new RepositoryDto();
        dto.setName(repository.getName());
        dto.setOwner(repository.getOwner().getLogin());
        dto.setFork(repository.isFork());
        List<BranchDto> branches = fetchBranchesForRepository(repository.getName());
        dto.setBranches(branches);

        return dto;
    }

    private List<BranchDto> fetchBranchesForRepository(String repoName) {
        String url = String.format("%s/repos/%s/branches", githubApiUrl, repoName);
        ResponseEntity<Branch[]> response;
        try {
            response = restTemplate.getForEntity(url, Branch[].class);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Collections.emptyList();
            } else {
                throw e;
            }
        }

        Branch[] branches = response.getBody();
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
}

