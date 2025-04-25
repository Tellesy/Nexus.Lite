package ly.neptune.nexus.lite.config;

import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

@Service
public class ConfigService {
    private static final String CONFIG_FILE_PATH = "config/application-gui.yml";
    private final Yaml yaml = new Yaml();

    @SuppressWarnings("unchecked")
    public Map<String, Object> loadConfig() {
        try {
            File file = new File(CONFIG_FILE_PATH);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                InputStream in = getClass().getClassLoader().getResourceAsStream("application.yml");
                Map<String, Object> defaultConfig = yaml.load(in);
                try (FileWriter writer = new FileWriter(file)) {
                    yaml.dump(defaultConfig, writer);
                }
                return defaultConfig;
            } else {
                try (FileInputStream fis = new FileInputStream(file)) {
                    return yaml.load(fis);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config", e);
        }
    }

    public void saveConfig(Map<String, Object> config) {
        try {
            File file = new File(CONFIG_FILE_PATH);
            file.getParentFile().mkdirs();
            try (FileWriter writer = new FileWriter(file)) {
                yaml.dump(config, writer);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to save config", e);
        }
    }
}
