package eu.wswieciejutra.repo_checker;

import eu.wswieciejutra.repo_checker.repository.Repository;
import eu.wswieciejutra.repo_checker.repository.RepositoryInterface;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryRepository implements RepositoryInterface {
    private final Map<Long, Repository> repositoryMap = new ConcurrentHashMap<>();
    private final Map<String, Repository.Owner> ownerMap = new ConcurrentHashMap<>();
    private long currentId = 1;


    @Override
    public Repository save(Repository entity) {
        if (entity.getId() == null) {
            entity.setId(currentId++);
        }
        repositoryMap.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public List<Repository> findAllByOwnerLogin(String login) {
        List<Repository> repositories = new ArrayList<>();
        for (Repository repo : repositoryMap.values()) {
            if (repo.getOwner().getLogin().equals(login)) {
                repositories.add(repo);
            }
        }
        return repositories;
    }

    @Override
    public Repository.Owner save(Repository.Owner owner) {
        ownerMap.put(owner.getLogin(), owner);
        return owner;
    }

    @Override
    public boolean existsByOwnerLogin(String login) {
        return ownerMap.containsKey(login);
    }
}