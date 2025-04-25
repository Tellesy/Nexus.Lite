package ly.neptune.nexus.lite.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SystemPreferenceService {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public SystemPreferenceService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public String getPreference(String key, String defaultValue) {
        try {
            String sql = "SELECT preference_value FROM system_preferences WHERE preference_key = ?";
            return jdbcTemplate.queryForObject(sql, String.class, key);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    @Transactional
    public void savePreference(String key, String value) {
        // Check if the preference exists
        String checkSql = "SELECT COUNT(*) FROM system_preferences WHERE preference_key = ?";
        int count = jdbcTemplate.queryForObject(checkSql, Integer.class, key);

        if (count > 0) {
            // Update existing preference
            String updateSql = "UPDATE system_preferences SET preference_value = ? WHERE preference_key = ?";
            jdbcTemplate.update(updateSql, value, key);
        } else {
            // Insert new preference
            String insertSql = "INSERT INTO system_preferences (preference_key, preference_value) VALUES (?, ?)";
            jdbcTemplate.update(insertSql, key, value);
        }
    }

    public String getDatabaseType() {
        return getPreference("db.type", "h2");
    }

    public String getPrimaryColor() {
        return getPreference("theme.primary-color", "#1976d2");
    }

    public String getSecondaryColor() {
        return getPreference("theme.secondary-color", "#FFC107");
    }

    public String getThemeMode() {
        return getPreference("theme.mode", "light");
    }

    @Transactional
    public void saveThemeSettings(String primaryColor, String secondaryColor, String themeMode) {
        savePreference("theme.primary-color", primaryColor);
        savePreference("theme.secondary-color", secondaryColor);
        savePreference("theme.mode", themeMode);
    }
}
