package eu.wswieciejutra.repo_checker.service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RepositoryDto {

    private String name;
    private String owner;
    private boolean isFork;

    public RepositoryDto(String name, String owner, boolean isFork) {
        this.name = name;
        this.owner = owner;
        this.isFork = isFork;
    }
}
