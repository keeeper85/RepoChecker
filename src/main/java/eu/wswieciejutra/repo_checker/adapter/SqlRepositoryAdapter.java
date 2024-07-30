package eu.wswieciejutra.repo_checker.adapter;

import org.springframework.data.jpa.repository.JpaRepository;
import eu.wswieciejutra.repo_checker.repository.Repository;
import eu.wswieciejutra.repo_checker.repository.RepositoryInterface;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SqlRepositoryAdapter extends RepositoryInterface, JpaRepository<Repository, Long> {

    Optional<Repository> findByName(String name);
    @Query("SELECT r FROM Repository r WHERE r.owner.login = :login")
    List<Repository> findAllByOwnerLogin(@Param("login") String login);
    Repository save(Repository repository);

    boolean existsByOwnerLogin(String login);
    @Query("SELECT r.owner FROM Repository r WHERE r.name = :name")
    Repository.Owner findByOwnerLogin(@Param("name") String name);
    Repository.Owner save(Repository.Owner owner);

}
