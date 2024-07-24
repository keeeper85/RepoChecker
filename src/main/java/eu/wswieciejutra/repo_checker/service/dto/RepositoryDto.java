package eu.wswieciejutra.repo_checker.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import repository.Branch;

import java.util.List;

@Getter
@Setter
public class RepositoryDto {

    private String name;
    private String owner;
    private List<BranchDto> branches;
}
