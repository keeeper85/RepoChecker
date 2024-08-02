package eu.wswieciejutra;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Services {
    GITHUB("GitHub", "https://api.github.com"),
    GITLAB("GitLab", "https://gitlab.com/api/v4");

    private final String name;
    private final String apiUrl;
}
