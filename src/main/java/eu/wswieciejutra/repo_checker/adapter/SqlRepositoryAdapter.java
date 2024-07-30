package eu.wswieciejutra.repo_checker.adapter;

import org.springframework.data.jpa.repository.JpaRepository;
import repository.Repository;
import repository.RepositoryInterface;

public interface SqlRepositoryAdapter extends RepositoryInterface, JpaRepository<Repository, Long> {


}
