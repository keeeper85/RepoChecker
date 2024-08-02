package eu.wswieciejutra;

import eu.wswieciejutra.dto.BranchDto;
import eu.wswieciejutra.dto.RepositoryDto;
import eu.wswieciejutra.service.ServiceStrategyInterface;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Factory {

    public static Set<Branch> fromBranchesDto(List<BranchDto> branchesDto, Repository repository) {
        return branchesDto.stream()
                .map(branchDto -> fromBranchDto(branchDto, repository))
                .collect(Collectors.toUnmodifiableSet());
    }

    static Branch fromBranchDto(BranchDto branchDto, Repository repository) {
        Branch.Commit commit = new Branch.Commit();
        commit.setSha(branchDto.lastCommitSha());

        Branch branch = new Branch();
        branch.setName(branchDto.name());
        branch.setCommit(commit);
        branch.setRepository(repository);
        return branch;
    }

    public static RepositoryDto convertGitHubToDto(ServiceStrategyInterface service, Repository repository, String token) {
        RepositoryDto dto = prepareDto(repository);
        dto.setOwner(repository.getOwner().getLogin());
        List<BranchDto> branches = service.fetchBranchesForRepository(repository.getOwner().getLogin(), repository.getName(), token);
        dto.setBranches(branches);
        return dto;
    }

    public static RepositoryDto convertGitLabToDto(ServiceStrategyInterface service, Repository repository, String token) {
        RepositoryDto dto = prepareDto(repository);
        dto.setOwner(repository.getPathWithNamespace().split("/")[0]);
        dto.setFork(false);
        List<BranchDto> branches = service.fetchBranchesForRepository(repository.getPathWithNamespace().split("/")[0], repository.getName(), token);
        dto.setBranches(branches);
        return dto;
    }

    public static RepositoryDto convertCachedRepositoryToDto(Repository repository, List<Branch> branches) {
        RepositoryDto dto = prepareDto(repository);
        dto.setOwner(repository.getOwner().getLogin());
        List<BranchDto> branchesDto = branches.stream().map(Factory::convertToBranchDto).toList();
        dto.setBranches(branchesDto);
        return dto;
    }

    private static RepositoryDto prepareDto(Repository repository){
        RepositoryDto dto = new RepositoryDto();
        dto.setName(repository.getName());
        dto.setFork(repository.isFork());
        return dto;
    }

    public static BranchDto convertToBranchDto(Branch branch) {
        return new BranchDto(branch.getName(), branch.getCommit().getSha());
    }

    public static HttpEntity<String> createHttpEntity(String token) {
        HttpHeaders headers = new HttpHeaders();
        if (token != null && !token.isEmpty()) {
            headers.set("Authorization", "Bearer " + token);
        }
        return new HttpEntity<>(headers);
    }
}