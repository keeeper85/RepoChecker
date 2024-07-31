package eu.wswieciejutra.repo_checker.repository;

import java.util.List;

public interface RepositoryInterface {

    Repository save(Repository repository);
    List<Repository> findAllByOwnerLogin(String name);

    Repository.Owner save(Repository.Owner owner);
    boolean existsByOwnerLogin(String login);

}
