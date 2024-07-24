package repository;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class Branch {

    private String name;
    private Commit commit;

    @Getter
    @Setter
    public static class Commit {
        private String sha;
    }
}
