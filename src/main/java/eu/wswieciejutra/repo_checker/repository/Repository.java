package eu.wswieciejutra.repo_checker.repository;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@Entity
@NoArgsConstructor
public class Repository {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @JsonProperty("path_with_namespace")
    private String pathWithNamespace;

    @JsonProperty("owner")
    @ManyToOne(cascade = CascadeType.ALL)
    private Owner owner;

    @OneToMany(mappedBy = "repository", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Branch> branches = new HashSet<>();

    @JsonProperty("fork")
    private boolean isFork;

    public void addBranches(Collection<Branch> branches) {
        this.branches.addAll(branches);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Repository that = (Repository) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(owner, that.owner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, owner);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Getter
    @Setter
    @Entity
    @Table(name = "repository_owner")
    @NoArgsConstructor
    public static class Owner {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        private String login;
        @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
        private Set<Repository> repositories;
    }
}