package eu.wswieciejutra.repo_checker.repository;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    @ManyToOne
    private Owner owner;

    @JsonProperty("fork")
    private boolean isFork;

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Getter
    @Setter
    @Entity
    @Table(name="repository_owner")
    @NoArgsConstructor
    public static class Owner {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        private String login;

    }

}

