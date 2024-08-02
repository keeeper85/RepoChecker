package eu.wswieciejutra;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@Entity
@NoArgsConstructor
public class Branch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private Commit commit;
    @ManyToOne
    @JoinColumn(name = "repository_id")
    private Repository repository;

    @Getter
    @Setter
    @Entity
    @Table(name = "last_commit")
    @NoArgsConstructor
    public static class Commit {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        private String sha;
        @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
        private Branch branch;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Branch branch = (Branch) o;
        return Objects.equals(id, branch.id) && Objects.equals(name, branch.name) && Objects.equals(repository, branch.repository);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, repository);
    }

}