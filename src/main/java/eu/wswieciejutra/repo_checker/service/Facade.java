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

        if (searchResultCachingService.hasRepositoryBeenCached(username)) System.err.println("Cached!");

        Services serviceType = Services.valueOf(service.toUpperCase());

        for (Services s : Services.values()) {
            if (serviceType.equals(s)) {
                List<RepositoryDto> repositories = factory.getService(s).getNonForkRepositories(username, token);
                searchResultCachingService.cacheRepositories(repositories);
                return repositories;
            }
        }
        List<RepositoryDto> repositories = factory.getService(Services.GITHUB).getNonForkRepositories(username, token);
        searchResultCachingService.cacheRepositories(repositories);
        return repositories;
    }
}
