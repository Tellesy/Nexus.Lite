package ly.neptune.nexus.lite.ui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.theme.lumo.LumoUtility;
import ly.neptune.nexus.lite.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

@Route(value = "change-password", layout = MainLayout.class)
@PageTitle("Change Password | Nexus Lite")
public class ChangePasswordView extends VerticalLayout {

    private final UserService userService;
    
    private PasswordField currentPassword;
    private PasswordField newPassword;
    private PasswordField confirmPassword;
    
    @Autowired
    public ChangePasswordView(UserService userService) {
        this.userService = userService;
        
        setPadding(true);
        setSpacing(true);
        setMaxWidth("500px");
        setAlignItems(Alignment.CENTER);
        
        H2 title = new H2("Change Password");
        title.addClassNames(LumoUtility.Margin.Top.MEDIUM, LumoUtility.Margin.Bottom.MEDIUM);
        
        currentPassword = new PasswordField("Current Password");
        currentPassword.setWidthFull();
        currentPassword.setRequired(true);
        
        newPassword = new PasswordField("New Password");
        newPassword.setWidthFull();
        newPassword.setRequired(true);
        
        confirmPassword = new PasswordField("Confirm New Password");
        confirmPassword.setWidthFull();
        confirmPassword.setRequired(true);
        
        Button changeButton = new Button("Change Password");
        changeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        changeButton.setWidthFull();
        changeButton.addClickListener(e -> changePassword());
        
        add(title, currentPassword, newPassword, confirmPassword, changeButton);
        
        // Check if using default password
        if (userService.isDefaultAdminPasswordUnchanged()) {
            Notification notification = Notification.show(
                "You are using the default admin password. Please change it for security reasons.",
                5000,
                Notification.Position.TOP_CENTER
            );
            notification.addThemeVariants(NotificationVariant.LUMO_WARNING);
        }
    }
    
    private void changePassword() {
        if (newPassword.isEmpty() || confirmPassword.isEmpty() || currentPassword.isEmpty()) {
            Notification.show("All fields are required")
                .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }
        
        if (!newPassword.getValue().equals(confirmPassword.getValue())) {
            Notification.show("New passwords don't match")
                .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }
        
        // Get current user
        String username = getCurrentUsername();
        if (username == null) {
            Notification.show("User not authenticated")
                .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }
        
        try {
            // Validate current password
            if (!userService.validateCurrentPassword(username, currentPassword.getValue())) {
                Notification.show("Current password is incorrect")
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }
            
            // Change password
            userService.changePassword(username, newPassword.getValue());
            
            Notification.show("Password changed successfully")
                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            
            // Clear fields
            currentPassword.clear();
            newPassword.clear();
            confirmPassword.clear();
            
        } catch (Exception e) {
            Notification.show("Error changing password: " + e.getMessage())
                .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
    
    private String getCurrentUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else {
            return principal.toString();
        }
    }
}
