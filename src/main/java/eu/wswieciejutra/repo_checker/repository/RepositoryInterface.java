package eu.wswieciejutra.repo_checker.repository;

import java.util.Optional;

public interface RepositoryInterface {

    Optional<Repository> findByName(String name);
    Repository save(Repository repository);

    Repository.Owner save(Repository.Owner owner);
    Repository.Owner findByOwnerLogin(String login);
    boolean existsByOwnerLogin(String login);

}
