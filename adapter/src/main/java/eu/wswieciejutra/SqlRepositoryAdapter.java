package eu.wswieciejutra;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SqlRepositoryAdapter extends RepositoryInterface, JpaRepository<Repository, Long> {

    @Query("SELECT r FROM Repository r WHERE r.owner.login = :login")
    List<Repository> findAllByOwnerLogin(@Param("login") String login);
    Repository save(Repository repository);

    boolean existsByOwnerLogin(String login);
    Repository.Owner save(Repository.Owner owner);

}
