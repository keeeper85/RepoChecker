package eu.wswieciejutra.repo_checker.service;

import eu.wswieciejutra.repo_checker.exception.UserNotFoundException;
import eu.wswieciejutra.repo_checker.service.dto.RepositoryDto;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Facade {

    private final Factory factory;
    private final SearchResultCachingService searchResultCachingService;
    private final boolean caching;

    public Facade(Factory factory, SearchResultCachingService searchResultCachingService, @Value("${repo-checker.caching}") boolean caching) {
        this.factory = factory;
        this.searchResultCachingService = searchResultCachingService;
        this.caching = caching;
    }

    public List<RepositoryDto> getNonForkRepositories(String service, String username, String token) throws UserNotFoundException {

        if (caching && searchResultCachingService.hasRepositoryBeenCached(username)) {
            return searchResultCachingService.getCachedRepositories(username)
                    .stream()
                    .map(repo -> Factory.convertCachedRepositoryToDto(repo, searchResultCachingService.getCachedBranches(repo)))
                    .toList();
        }

        Services serviceType = Services.valueOf(service.toUpperCase());
        List<RepositoryDto> repositories = factory.getService(serviceType).getNonForkRepositories(username, token);
        if(caching) searchResultCachingService.cacheRepositories(repositories);
        return repositories;
    }
}
