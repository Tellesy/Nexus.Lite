package ly.neptune.nexus.lite.ui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import ly.neptune.nexus.lite.config.ConfigService;

import java.util.HashMap;
import java.util.Map;

@Route(value = "config", layout = MainLayout.class)
@PageTitle("Configuration | Nexus Lite")
public class ConfigurationView extends VerticalLayout {
    public ConfigurationView(ConfigService configService) {
        Map<String, Object> cfg = configService.loadConfig();
        // Spring.datasource.hikari
        Map<String,Object> spring = (Map<String,Object>) cfg.getOrDefault("spring", new HashMap<>());
        Map<String,Object> ds = (Map<String,Object>) spring.getOrDefault("datasource", new HashMap<>());
        Map<String,Object> hikari = (Map<String,Object>) ds.getOrDefault("hikari", new HashMap<>());

        TextField jdbcUrl = new TextField("JDBC URL", (String) hikari.getOrDefault("jdbc-url", ""));
        TextField dbUser = new TextField("DB Username", (String) hikari.getOrDefault("username", ""));
        PasswordField dbPass = new PasswordField("DB Password", (String) hikari.getOrDefault("password", ""));
        TextField driver = new TextField("Driver Class", (String) hikari.getOrDefault("driver-class-name", ""));
        NumberField maxPool = new NumberField("Max Pool Size"); maxPool.setValue(((Number) hikari.getOrDefault("maximum-pool-size", 0)).doubleValue());
        NumberField minIdle = new NumberField("Min Idle"); minIdle.setValue(((Number) hikari.getOrDefault("minimum-idle", 0)).doubleValue());
        NumberField connTimeout = new NumberField("Connection Timeout"); connTimeout.setValue(((Number) hikari.getOrDefault("connection-timeout", 0)).doubleValue());
        NumberField idleTimeout = new NumberField("Idle Timeout"); idleTimeout.setValue(((Number) hikari.getOrDefault("idle-timeout", 0)).doubleValue());
        NumberField maxLifetime = new NumberField("Max Lifetime"); maxLifetime.setValue(((Number) hikari.getOrDefault("max-lifetime", 0)).doubleValue());

        // FCUBS
        Map<String,Object> fcubs = (Map<String,Object>) cfg.getOrDefault("fcubs", new HashMap<>());
        TextField wsdl = new TextField("FCUBS WSDL URL", (String) fcubs.getOrDefault("wsdl-url", ""));
        Map<String,Object> header = (Map<String,Object>) fcubs.getOrDefault("header", new HashMap<>());
        TextField fcubsUser = new TextField("FCUBS User ID", (String) header.getOrDefault("user-id", ""));
        TextField branch = new TextField("Branch", (String) header.getOrDefault("branch", ""));
        TextField source = new TextField("Source", (String) header.getOrDefault("source", ""));
        TextField ubscomp = new TextField("UBSCOMP", (String) header.getOrDefault("ubscomp", ""));

        FormLayout form = new FormLayout();
        form.add(jdbcUrl, dbUser, dbPass, driver,
                 maxPool, minIdle, connTimeout, idleTimeout, maxLifetime,
                 wsdl, fcubsUser, branch, source, ubscomp);

        Button save = new Button("Save", e -> {
            Map<String,Object> newCfg = new HashMap<>();
            Map<String,Object> springMap = new HashMap<>();
            Map<String,Object> dsMap = new HashMap<>();
            Map<String,Object> hikariMap = new HashMap<>();
            hikariMap.put("jdbc-url", jdbcUrl.getValue());
            hikariMap.put("username", dbUser.getValue());
            hikariMap.put("password", dbPass.getValue());
            hikariMap.put("driver-class-name", driver.getValue());
            hikariMap.put("maximum-pool-size", maxPool.getValue().intValue());
            hikariMap.put("minimum-idle", minIdle.getValue().intValue());
            hikariMap.put("connection-timeout", connTimeout.getValue().longValue());
            hikariMap.put("idle-timeout", idleTimeout.getValue().longValue());
            hikariMap.put("max-lifetime", maxLifetime.getValue().longValue());
            dsMap.put("hikari", hikariMap);
            springMap.put("datasource", dsMap);
            newCfg.put("spring", springMap);

            Map<String,Object> fcubsMap = new HashMap<>();
            fcubsMap.put("wsdl-url", wsdl.getValue());
            Map<String,Object> headerMap = new HashMap<>();
            headerMap.put("user-id", fcubsUser.getValue());
            headerMap.put("branch", branch.getValue());
            headerMap.put("source", source.getValue());
            headerMap.put("ubscomp", ubscomp.getValue());
            fcubsMap.put("header", headerMap);
            newCfg.put("fcubs", fcubsMap);

            configService.saveConfig(newCfg);
            Notification.show("Configuration saved", 2000, Notification.Position.MIDDLE);
        });

        add(form, save);
    }
}
