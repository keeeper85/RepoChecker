package eu.wswieciejutra.repo_checker.repository;

import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RepositoryInterface {

    Optional<Repository> findByName(String name);
    Repository save(Repository repository);
    List<Repository> findAllByOwnerLogin(String name);

    Repository.Owner save(Repository.Owner owner);
    Repository.Owner findByOwnerLogin(String login);
    boolean existsByOwnerLogin(String login);

}
