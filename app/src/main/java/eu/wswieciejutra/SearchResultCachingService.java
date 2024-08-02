package eu.wswieciejutra;

import eu.wswieciejutra.dto.RepositoryDto;
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
        if (repositories.isEmpty()) {return;}
        String username = repositories.getFirst().getOwner();
        if (repositoryInterface.existsByOwnerLogin(username)) return;

        Repository.Owner owner = new Repository.Owner();
        owner.setLogin(username);
        repositoryInterface.save(owner);

        for (RepositoryDto dto : repositories) {
            Repository repository = new Repository();
            repository.setName(dto.getName());
            repository.setFork(dto.isFork());
            repository.setOwner(owner);

            Set<Branch> branches = Factory.fromBranchesDto(dto.getBranches(), repository);
            repository.addBranches(branches);
            repositoryInterface.save(repository);
        }
        LoggerUtility.LOGGER.info("Repositories cached for {}", username);
    }

    public boolean hasRepositoryBeenCached(String username) {
        return repositoryInterface.existsByOwnerLogin(username);
    }

    public List<Repository> getCachedRepositories(String username) {
        LoggerUtility.LOGGER.info("Repositories retrieved from cache for {}", username);
        return repositoryInterface.findAllByOwnerLogin(username);
    }

    public List<Branch> getCachedBranches(Repository repository) {
        return branchInterface.findAllBranchesByRepository(repository);
    }
}
