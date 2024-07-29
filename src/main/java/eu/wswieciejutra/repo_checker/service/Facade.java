package eu.wswieciejutra.repo_checker.service;

import eu.wswieciejutra.repo_checker.exception.UserNotFoundException;
import eu.wswieciejutra.repo_checker.service.dto.RepositoryDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Facade {

    private final Factory factory;

    public Facade(Factory factory) {
        this.factory = factory;
    }

    public List<RepositoryDto> getNonForkRepositories(String service, String username, String token) throws UserNotFoundException {

        Services serviceType = Services.valueOf(service.toUpperCase());

        for (Services s : Services.values()) {
            if (serviceType.equals(s)) {
                return factory.getService(s).getNonForkRepositories(username, token);
            }
        }
        return factory.getService(Services.GITHUB).getNonForkRepositories(username, token);
    }
}
