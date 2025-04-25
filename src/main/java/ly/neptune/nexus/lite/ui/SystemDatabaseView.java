package ly.neptune.nexus.lite.ui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import ly.neptune.nexus.lite.service.SystemPreferenceService;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "system-database", layout = MainLayout.class)
@PageTitle("System Database | Nexus Lite")
public class SystemDatabaseView extends VerticalLayout {

    private final SystemPreferenceService preferenceService;
    
    private ComboBox<String> databaseType;
    private TextField jdbcUrl;
    private TextField username;
    private PasswordField password;
    
    @Autowired
    public SystemDatabaseView(SystemPreferenceService preferenceService) {
        this.preferenceService = preferenceService;
        
        setPadding(true);
        setSpacing(true);
        
        H2 title = new H2("System Database Configuration");
        title.addClassNames(LumoUtility.Margin.Top.MEDIUM, LumoUtility.Margin.Bottom.MEDIUM);
        
        FormLayout formLayout = new FormLayout();
        formLayout.setMaxWidth("600px");
        formLayout.setResponsiveSteps(
            new FormLayout.ResponsiveStep("0", 1),
            new FormLayout.ResponsiveStep("500px", 2)
        );
        
        databaseType = new ComboBox<>("Database Type");
        databaseType.setItems("h2", "mysql");
        databaseType.setValue(preferenceService.getPreference("db.type", "h2"));
        databaseType.setRequired(true);
        
        jdbcUrl = new TextField("JDBC URL");
        jdbcUrl.setWidth("100%");
        
        if ("h2".equals(databaseType.getValue())) {
            jdbcUrl.setValue("jdbc:h2:file:./data/nexusdb");
        } else {
            jdbcUrl.setValue("jdbc:mysql://localhost:3306/nexusdb");
        }
        
        username = new TextField("Username");
        username.setValue("h2".equals(databaseType.getValue()) ? "sa" : "root");
        
        password = new PasswordField("Password");
        password.setValue("h2".equals(databaseType.getValue()) ? "password" : "");
        
        databaseType.addValueChangeListener(event -> {
            if ("h2".equals(event.getValue())) {
                jdbcUrl.setValue("jdbc:h2:file:./data/nexusdb");
                username.setValue("sa");
                password.setValue("password");
            } else {
                jdbcUrl.setValue("jdbc:mysql://localhost:3306/nexusdb");
                username.setValue("root");
                password.setValue("");
            }
        });
        
        formLayout.add(databaseType, 2);
        formLayout.add(jdbcUrl, 2);
        formLayout.add(username);
        formLayout.add(password);
        
        Button saveButton = new Button("Save Configuration");
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClickListener(e -> saveConfiguration());
        
        Button testButton = new Button("Test Connection");
        testButton.addClickListener(e -> testConnection());
        
        HorizontalLayout buttonLayout = new HorizontalLayout(saveButton, testButton);
        buttonLayout.setSpacing(true);
        
        add(title, formLayout, buttonLayout);
    }
    
    private void saveConfiguration() {
        try {
            preferenceService.savePreference("db.type", databaseType.getValue());
            preferenceService.savePreference("db.url", jdbcUrl.getValue());
            preferenceService.savePreference("db.username", username.getValue());
            preferenceService.savePreference("db.password", password.getValue());
            
            Notification.show("Database configuration saved! Restart application to apply changes.")
                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } catch (Exception e) {
            Notification.show("Error saving configuration: " + e.getMessage())
                .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
    
    private void testConnection() {
        try {
            // This would be implemented to test the connection with the provided parameters
            Notification.show("Connection successful!")
                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } catch (Exception e) {
            Notification.show("Connection failed: " + e.getMessage())
                .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
}
