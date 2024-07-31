package eu.wswieciejutra.repo_checker.service;

import eu.wswieciejutra.repo_checker.exception.UserNotFoundException;
import eu.wswieciejutra.repo_checker.service.dto.RepositoryDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class Facade {

    private final Factory factory;
    private final SearchResultCachingService searchResultCachingService;

    public List<RepositoryDto> getNonForkRepositories(String service, String username, String token) throws UserNotFoundException {

        if (searchResultCachingService.hasRepositoryBeenCached(username)) {
            return searchResultCachingService.getCachedRepositories(username)
                    .stream()
                    .map(repo -> Factory.convertCachedRepositoryToDto(repo, searchResultCachingService.getCachedBranches(repo)))
                    .toList();
        }

        Services serviceType = Services.valueOf(service.toUpperCase());
        List<RepositoryDto> repositories = factory.getService(serviceType).getNonForkRepositories(username, token);
        searchResultCachingService.cacheRepositories(repositories);
        return repositories;
    }
}
