package ly.neptune.nexus.lite.ui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import ly.neptune.nexus.lite.service.SystemPreferenceService;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "theme", layout = MainLayout.class)
@PageTitle("Theme Settings | Nexus Lite")
public class ThemeView extends VerticalLayout {

    private final SystemPreferenceService preferenceService;
    
    private TextField primaryColor;
    private TextField secondaryColor;
    private ComboBox<String> themeMode;
    
    @Autowired
    public ThemeView(SystemPreferenceService preferenceService) {
        this.preferenceService = preferenceService;
        
        setPadding(true);
        setSpacing(true);
        
        H2 title = new H2("Theme Settings");
        title.addClassNames(LumoUtility.Margin.Top.MEDIUM, LumoUtility.Margin.Bottom.MEDIUM);
        
        // Primary color
        primaryColor = new TextField("Primary Color");
        primaryColor.setValue(preferenceService.getPrimaryColor());
        primaryColor.setHelperText("Example: #1976d2, blue, etc.");
        
        // Secondary color
        secondaryColor = new TextField("Secondary Color");
        secondaryColor.setValue(preferenceService.getSecondaryColor());
        secondaryColor.setHelperText("Example: #FFC107, amber, etc.");
        
        // Theme mode
        themeMode = new ComboBox<>("Theme Mode");
        themeMode.setItems("light", "dark");
        themeMode.setValue(preferenceService.getThemeMode());
        
        // Color preview
        VerticalLayout primaryPreview = createColorPreview("Primary Color", primaryColor.getValue());
        VerticalLayout secondaryPreview = createColorPreview("Secondary Color", secondaryColor.getValue());
        
        primaryColor.addValueChangeListener(e -> 
            primaryPreview.getStyle().set("background-color", e.getValue()));
            
        secondaryColor.addValueChangeListener(e -> 
            secondaryPreview.getStyle().set("background-color", e.getValue()));
        
        // Action buttons
        Button saveButton = new Button("Save Settings");
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClickListener(e -> saveThemeSettings());
        
        Button resetButton = new Button("Reset to Default");
        resetButton.addClickListener(e -> resetToDefault());
        
        // Layouts
        HorizontalLayout previewLayout = new HorizontalLayout(primaryPreview, secondaryPreview);
        previewLayout.setWidth("100%");
        previewLayout.setHeight("120px");
        
        HorizontalLayout buttonLayout = new HorizontalLayout(saveButton, resetButton);
        buttonLayout.setSpacing(true);
        
        // Add components to view
        add(title, primaryColor, secondaryColor, themeMode, previewLayout, buttonLayout);
    }
    
    private VerticalLayout createColorPreview(String label, String color) {
        VerticalLayout layout = new VerticalLayout();
        layout.setWidth("200px");
        layout.setHeight("100px");
        layout.getStyle().set("background-color", color);
        layout.getStyle().set("border-radius", "8px");
        layout.getStyle().set("box-shadow", "0 2px 4px rgba(0,0,0,0.1)");
        layout.getStyle().set("color", "#fff");
        layout.getStyle().set("text-align", "center");
        layout.getStyle().set("justify-content", "center");
        layout.getStyle().set("font-weight", "bold");
        layout.add(label);
        return layout;
    }
    
    private void saveThemeSettings() {
        try {
            preferenceService.saveThemeSettings(
                primaryColor.getValue(),
                secondaryColor.getValue(),
                themeMode.getValue()
            );
            
            // Generate and inject custom CSS
            injectCustomThemeCSS();
            
            Notification.show("Theme settings saved! Refresh the page to see changes.")
                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } catch (Exception e) {
            Notification.show("Error saving theme settings: " + e.getMessage())
                .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
    
    private void resetToDefault() {
        primaryColor.setValue("#1976d2");
        secondaryColor.setValue("#FFC107");
        themeMode.setValue("light");
    }
    
    private void injectCustomThemeCSS() {
        // This would inject the custom CSS into the page, but in a real app
        // we would need to use something like a CustomTheme class in Vaadin
        String css = String.format("""
            :root {
                --lumo-primary-color: %s;
                --lumo-primary-text-color: %s;
                --lumo-secondary-color: %s;
                --lumo-secondary-text-color: %s;
            }
            """, 
            primaryColor.getValue(), primaryColor.getValue(),
            secondaryColor.getValue(), secondaryColor.getValue()
        );
        
        getElement().executeJs("""
            const style = document.createElement('style');
            style.textContent = $0;
            document.head.appendChild(style);
            """, css);
    }
}
