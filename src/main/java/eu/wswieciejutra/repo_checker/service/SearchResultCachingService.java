package eu.wswieciejutra.repo_checker.service;

import eu.wswieciejutra.repo_checker.repository.Branch;
import eu.wswieciejutra.repo_checker.repository.BranchInterface;
import eu.wswieciejutra.repo_checker.repository.Repository;
import eu.wswieciejutra.repo_checker.repository.RepositoryInterface;
import eu.wswieciejutra.repo_checker.service.dto.BranchDto;
import eu.wswieciejutra.repo_checker.service.dto.RepositoryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class SearchResultCachingService {

    private final RepositoryInterface repositoryInterface;
    private final BranchInterface branchInterface;

    public void cacheRepositories(List<RepositoryDto> repositories) {
        String username = repositories.get(0).getOwner();
        if (repositoryInterface.existsByOwnerLogin(username)) return;

        Repository.Owner owner = new Repository.Owner();
        owner.setLogin(username);

        for (RepositoryDto dto : repositories) {
            Repository repository = new Repository();
            repository.setName(dto.getName());
            repository.setFork(dto.isFork());
            repository.setOwner(owner);

            Set<Branch> branches = Factory.fromBranchesDto(dto.getBranches(), repository);
            repository.setBranches(branches);

            // Save repository along with branches
            repositoryInterface.save(repository);
        }
    }

    public boolean hasRepositoryBeenCached(String username) {
        return repositoryInterface.existsByOwnerLogin(username);
    }

    public List<Repository> getCachedRepositories(String username) {
        List<Repository> repos = repositoryInterface.findAllByOwnerLogin(username);
        System.err.println("Getting repositories");
        repos.forEach(System.out::println);
        return repos;
    }

    public List<Branch> getCachedBranches(Repository repository) {
        List<Branch> branches = branchInterface.findAllBranchesByRepository(repository);
        System.err.println("Getting branches");
        branches.forEach(System.out::println);
        return branches;
    }
}
