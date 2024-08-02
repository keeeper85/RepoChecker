package eu.wswieciejutra.strategy;

import eu.wswieciejutra.*;
import eu.wswieciejutra.dto.BranchDto;
import eu.wswieciejutra.dto.RepositoryDto;
import eu.wswieciejutra.exception.ApiLimitReachedException;
import eu.wswieciejutra.exception.UserNotFoundException;
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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GitHubStrategy implements CodeRepositoryService {

    private final RestTemplate restTemplate;
    private final String apiUrl = Services.GITHUB.getApiUrl();

    @Autowired
    public GitHubStrategy(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public GitHubStrategy(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public List<RepositoryDto> getNonForkRepositories(String username, String token) throws UserNotFoundException, ApiLimitReachedException {
        String url = UriComponentsBuilder.fromHttpUrl(apiUrl)
                .pathSegment("users", username, "repos")
                .toUriString();

        HttpEntity<String> entity = Factory.createHttpEntity(token);

        try {
            ResponseEntity<Repository[]> response = restTemplate.exchange(url, HttpMethod.GET, entity, Repository[].class);
            Repository[] repositories = response.getBody();
            if (repositories == null || repositories.length == 0) {
                LoggerUtility.LOGGER.info("No repositories found");
                return List.of();
            }

            return Arrays.stream(repositories)
                    .filter(repo -> !repo.isFork())
                    .map(repo -> Factory.convertGitHubToDto(this, repo, token))
                    .collect(Collectors.toList());
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new UserNotFoundException("User not found: " + e.getMessage());
            }
            else if (e.getStatusCode() == HttpStatus.FORBIDDEN) {
                throw new ApiLimitReachedException("Api limit reached" + e.getMessage());
            }
            else {
                throw e;
            }
        }
    }

    public List<BranchDto> fetchBranchesForRepository(String owner, String repoName, String token) {
        String url = String.format("%s/repos/%s/%s/branches", apiUrl, owner, repoName);
        return ServiceHelper.getBranches(restTemplate, url, token);
    }


}
