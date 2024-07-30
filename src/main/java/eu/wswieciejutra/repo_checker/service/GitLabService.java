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
import eu.wswieciejutra.repo_checker.repository.Branch;
import eu.wswieciejutra.repo_checker.repository.Repository;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GitLabService implements CodeRepositoryService{

    private final RestTemplate restTemplate;
    private final String apiUrl = Services.GITLAB.getApiUrl();

    @Autowired
    public GitLabService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public GitLabService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public List<RepositoryDto> getNonForkRepositories(String username, String token) throws UserNotFoundException {
        String url = UriComponentsBuilder.fromHttpUrl(apiUrl)
                .pathSegment("users", username, "projects")
                .toUriString();

        HttpEntity<String> entity = Factory.createHttpEntity(token);

        try {
            ResponseEntity<Repository[]> response = restTemplate.exchange(url, HttpMethod.GET, entity, Repository[].class);
            Repository[] repositories = response.getBody();

            return Arrays.stream(repositories)
                    .map(repo -> Factory.convertGitLabToDto(this, repo, token))
                    .collect(Collectors.toList());
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new UserNotFoundException("User not found");
            } else {
                throw e;
            }
        }
    }

    @Override
    public List<BranchDto> fetchBranchesForRepository(String owner, String repoName, String token) {
        String url = String.format("%s/projects/%s/repository/branches", apiUrl, owner + "%2F" + repoName);

        HttpEntity<String> entity = Factory.createHttpEntity(token);

        ResponseEntity<Branch[]> response;
        try {
            response = restTemplate.exchange(url, HttpMethod.GET, entity, Branch[].class);
            if (response.getBody() == null) {
                return Collections.emptyList();
            }
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Collections.emptyList();
            } else {
                throw e;
            }
        }

        Branch[] branches = response.getBody();

        return (branches != null) ? Arrays.stream(branches)
                .map(branch -> Factory.convertToBranchDto(branch))
                .collect(Collectors.toList())
                : Collections.emptyList();
    }

}
