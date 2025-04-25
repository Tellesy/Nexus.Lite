package ly.neptune.nexus.lite.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Long id;
    private String username;
    private String password;
    private String email;
    private boolean enabled;
    private LocalDateTime createdAt;
    private LocalDateTime lastModified;
    private Set<String> roles;
}
