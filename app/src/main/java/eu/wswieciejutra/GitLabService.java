package eu.wswieciejutra;

import eu.wswieciejutra.dto.BranchDto;
import eu.wswieciejutra.dto.RepositoryDto;
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
            if (repositories == null || repositories.length == 0) {
                LoggerUtility.LOGGER.info("No repositories found");
                return List.of();
            }

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
        return ServiceHelper.getBranches(restTemplate, url, token);
    }
}
