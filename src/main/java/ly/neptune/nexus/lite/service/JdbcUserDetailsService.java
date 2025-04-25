package ly.neptune.nexus.lite.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JdbcUserDetailsService implements UserDetailsService {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public JdbcUserDetailsService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String sql = "SELECT u.username, u.password, u.enabled FROM users u WHERE u.username = ?";
        
        try {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
                String fetchedUsername = rs.getString("username");
                String password = rs.getString("password");
                boolean enabled = rs.getBoolean("enabled");
                
                List<GrantedAuthority> authorities = getUserAuthorities(fetchedUsername);
                
                return User.builder()
                    .username(fetchedUsername)
                    .password(password)
                    .disabled(!enabled)
                    .accountExpired(false)
                    .accountLocked(false)
                    .credentialsExpired(false)
                    .authorities(authorities)
                    .build();
            }, username);
        } catch (Exception e) {
            throw new UsernameNotFoundException("User not found: " + username, e);
        }
    }
    
    private List<GrantedAuthority> getUserAuthorities(String username) {
        String sql = "SELECT r.name FROM roles r " +
                     "JOIN user_roles ur ON r.id = ur.role_id " +
                     "JOIN users u ON u.id = ur.user_id " +
                     "WHERE u.username = ?";
        
        return jdbcTemplate.query(sql, (rs, rowNum) ->
            new SimpleGrantedAuthority(rs.getString("name")), username);
    }
}
