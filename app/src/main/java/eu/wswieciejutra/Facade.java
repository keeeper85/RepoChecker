package eu.wswieciejutra;

import eu.wswieciejutra.dto.RepositoryDto;
import eu.wswieciejutra.exception.ApiLimitReachedException;
import eu.wswieciejutra.exception.UserNotFoundException;
import eu.wswieciejutra.service.ServiceHelper;
import eu.wswieciejutra.service.Services;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Facade {

    private final ServiceHelper serviceHelper;
    private final SearchResultCachingService searchResultCachingService;
    private final boolean caching;

    public Facade(ServiceHelper serviceHelper, SearchResultCachingService searchResultCachingService, @Value("${repo-checker.caching}") boolean caching) {
        this.serviceHelper = serviceHelper;
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

        Services serviceType = service != null ? Services.valueOf(service.toUpperCase()) : Services.GITHUB;
        List<RepositoryDto> repositories = serviceHelper.getService(serviceType).getNonForkRepositories(username, token);
        if(caching) searchResultCachingService.cacheRepositories(repositories);
        return repositories;
    }
}
