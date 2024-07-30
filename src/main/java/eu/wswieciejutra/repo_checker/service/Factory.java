package eu.wswieciejutra.repo_checker.service;

import eu.wswieciejutra.repo_checker.service.dto.BranchDto;
import eu.wswieciejutra.repo_checker.service.dto.RepositoryDto;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import eu.wswieciejutra.repo_checker.repository.Branch;
import eu.wswieciejutra.repo_checker.repository.Repository;

import java.util.List;

@Component
public class Factory {

    private final GitHubService gitHubService;
    private final GitLabService gitLabService;

    public Factory(GitHubService gitHubService, GitLabService gitLabService) {
        this.gitHubService = gitHubService;
        this.gitLabService = gitLabService;
    }

    public CodeRepositoryService getService(Services service){
        switch (service) {
            case GITHUB:
                return gitHubService;
            case GITLAB:
                return gitLabService;
            default:
                throw new IllegalArgumentException("Unknown service: " + service);
        }
    }

    public static RepositoryDto convertGitHubToDto(CodeRepositoryService service, Repository repository, String token) {
        RepositoryDto dto = new RepositoryDto();
        dto.setName(repository.getName());
        dto.setOwner(repository.getOwner().getLogin());
        dto.setFork(repository.isFork());
        List<BranchDto> branches = service.fetchBranchesForRepository(repository.getOwner().getLogin(), repository.getName(), token);
        dto.setBranches(branches);

        return dto;
    }

    public static RepositoryDto convertGitLabToDto(CodeRepositoryService service, Repository repository, String token) {
        RepositoryDto dto = new RepositoryDto();
        dto.setName(repository.getName());
        dto.setOwner(repository.getPathWithNamespace().split("/")[0]);
        dto.setFork(false);
        List<BranchDto> branches = service.fetchBranchesForRepository(repository.getPathWithNamespace().split("/")[0], repository.getName(), token);
        dto.setBranches(branches);

        return dto;
    }

    public static BranchDto convertToBranchDto(Branch branch) {
        BranchDto dto = new BranchDto();
        dto.setName(branch.getName());
        dto.setLastCommitSha(branch.getCommit().getSha());
        return dto;
    }

    public static HttpEntity<String> createHttpEntity(String token) {
        HttpHeaders headers = new HttpHeaders();
        if (token != null && !token.isEmpty()) {
            headers.set("Authorization", "Bearer " + token);
        }
        return new HttpEntity<>(headers);
    }
}
