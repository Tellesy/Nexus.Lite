package ly.neptune.nexus.lite.service;

import ly.neptune.nexus.lite.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {
    
    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;
    
    @Autowired
    public UserService(JdbcTemplate jdbcTemplate, PasswordEncoder passwordEncoder) {
        this.jdbcTemplate = jdbcTemplate;
        this.passwordEncoder = passwordEncoder;
    }
    
    public Optional<User> findByUsername(String username) {
        String sql = "SELECT u.id, u.username, u.password, u.email, u.enabled, u.created_at, u.last_modified " +
                     "FROM users u WHERE u.username = ?";
        
        List<User> users = jdbcTemplate.query(sql, (rs, rowNum) -> {
            User user = new User();
            user.setId(rs.getLong("id"));
            user.setUsername(rs.getString("username"));
            user.setPassword(rs.getString("password"));
            user.setEmail(rs.getString("email"));
            user.setEnabled(rs.getBoolean("enabled"));
            user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            user.setLastModified(rs.getTimestamp("last_modified").toLocalDateTime());
            user.setRoles(getUserRoles(user.getId()));
            return user;
        }, username);
        
        return users.isEmpty() ? Optional.empty() : Optional.of(users.get(0));
    }
    
    private Set<String> getUserRoles(Long userId) {
        String sql = "SELECT r.name FROM roles r " +
                     "JOIN user_roles ur ON r.id = ur.role_id " +
                     "WHERE ur.user_id = ?";
                     
        List<String> roles = jdbcTemplate.queryForList(sql, String.class, userId);
        return new HashSet<>(roles);
    }
    
    @Transactional
    public void changePassword(String username, String newPassword) {
        String encodedPassword = passwordEncoder.encode(newPassword);
        jdbcTemplate.update(
            "UPDATE users SET password = ?, last_modified = CURRENT_TIMESTAMP WHERE username = ?", 
            encodedPassword, username
        );
    }
    
    @Transactional
    public boolean validateCurrentPassword(String username, String currentPassword) {
        String sql = "SELECT password FROM users WHERE username = ?";
        String storedPassword = jdbcTemplate.queryForObject(sql, String.class, username);
        return passwordEncoder.matches(currentPassword, storedPassword);
    }
    
    @Transactional
    public void updateUserProfile(User user) {
        jdbcTemplate.update(
            "UPDATE users SET email = ?, last_modified = CURRENT_TIMESTAMP WHERE id = ?",
            user.getEmail(), user.getId()
        );
    }
    
    public boolean isDefaultAdminPasswordUnchanged() {
        Optional<User> adminUser = findByUsername("admin");
        if (adminUser.isPresent()) {
            // Check if the password is still the default one (admin)
            return passwordEncoder.matches("admin", adminUser.get().getPassword());
        }
        return false;
    }
}
