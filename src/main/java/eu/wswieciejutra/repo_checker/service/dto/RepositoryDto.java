package eu.wswieciejutra.repo_checker.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RepositoryDto {

    private String name;
    private String owner;
    @JsonIgnore
    private boolean isFork;
    private List<BranchDto> branches;
}
