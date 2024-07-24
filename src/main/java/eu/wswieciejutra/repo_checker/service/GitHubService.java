package eu.wswieciejutra.repo_checker.service;

import eu.wswieciejutra.repo_checker.exception.UserNotFoundException;
import eu.wswieciejutra.repo_checker.service.dto.BranchDto;
import eu.wswieciejutra.repo_checker.service.dto.RepositoryDto;
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

        String branchesUrl = githubApiUrl + "/repos/" + repository.getOwner().getLogin() + "/" + repository.getName() + "/branches";
        ResponseEntity<Branch[]> branchesResponse = restTemplate.getForEntity(branchesUrl, Branch[].class);
        Branch[] branches = branchesResponse.getBody();

        List<BranchDto> branchDtos = Arrays.stream(branches)
                .map(branch -> {
                    BranchDto branchDto = new BranchDto();
                    branchDto.setName(branch.getName());
                    branchDto.setLastCommitSha(branch.getCommit().getSha());
                    return branchDto;
                })
                .collect(Collectors.toList());

        dto.setBranches(branchDtos);

        return dto;
    }
}

