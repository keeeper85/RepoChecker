package eu.wswieciejutra;

import eu.wswieciejutra.dto.RepositoryDto;
import eu.wswieciejutra.exception.ApiLimitReachedException;
import eu.wswieciejutra.exception.UserNotFoundException;
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

    public List<RepositoryDto> getNonForkRepositories(String service, String username, String token) throws UserNotFoundException, ApiLimitReachedException {

        if (caching && searchResultCachingService.hasRepositoryBeenCached(username)) {
            return searchResultCachingService.getCachedRepositories(username)
                    .stream()
                    .map(repo -> Factory.convertCachedRepositoryToDto(repo, searchResultCachingService.getCachedBranches(repo)))
                    .toList();
        }

        Services serviceType = service == null ? Services.valueOf(service.toUpperCase()) : Services.GITHUB;
        List<RepositoryDto> repositories = factory.getService(serviceType).getNonForkRepositories(username, token);
        if(caching) searchResultCachingService.cacheRepositories(repositories);
        return repositories;
    }
}
