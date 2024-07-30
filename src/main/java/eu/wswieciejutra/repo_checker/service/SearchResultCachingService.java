package eu.wswieciejutra.repo_checker.service;

import eu.wswieciejutra.repo_checker.repository.Repository;
import eu.wswieciejutra.repo_checker.service.dto.RepositoryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import eu.wswieciejutra.repo_checker.repository.RepositoryInterface;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SearchResultCachingService {

    private final RepositoryInterface repositoryInterface;

    public void cacheRepositories(List<RepositoryDto> repositories){

        String username = repositories.getFirst().getOwner();
        if(repositoryInterface.existsByOwnerLogin(username)) return;

        Repository.Owner owner = new Repository.Owner();
        owner.setLogin(username);
        repositoryInterface.save(owner);

        for (RepositoryDto dto : repositories) {
            Repository repository = new Repository();
            repository.setName(dto.getName());
            repository.setFork(dto.isFork());
            repository.setOwner(owner);
            repositoryInterface.save(repository);
        }
    }

    public boolean hasRepositoryBeenCached(String username) {
        return repositoryInterface.existsByOwnerLogin(username);
    }
}
