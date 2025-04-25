package ly.neptune.nexus.lite.ui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

@Route("login")
@PageTitle("Login | Nexus Lite")
public class LoginView extends VerticalLayout {

    private final TextField username;
    private final PasswordField password;

    public LoginView() {
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        getStyle().set("background-color", "var(--lumo-contrast-5pct)");

        // Create the login form
        Div loginCard = new Div();
        loginCard.addClassNames(
            LumoUtility.Background.BASE,
            LumoUtility.BorderRadius.MEDIUM,
            LumoUtility.BoxShadow.MEDIUM,
            LumoUtility.Padding.LARGE
        );
        loginCard.setMaxWidth("400px");

        // Logo and application name
        Image logo = new Image("/images/logo.svg", "Nexus Lite Logo");
        logo.setHeight("64px");
        logo.getStyle().set("margin-bottom", "16px");

        H2 header = new H2("Welcome to Nexus Lite");
        header.addClassNames(
            LumoUtility.Margin.Bottom.MEDIUM,
            LumoUtility.TextAlignment.CENTER
        );

        // Form fields
        username = new TextField("Username");
        username.setPlaceholder("Enter your username");
        username.setWidth("100%");
        username.setValue("admin");  // Pre-fill for demo

        password = new PasswordField("Password");
        password.setPlaceholder("Enter your password");
        password.setWidth("100%");
        password.setValue("admin");  // Pre-fill for demo

        // Login button
        Button loginButton = new Button("Log in");
        loginButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        loginButton.setWidth("100%");
        loginButton.addClickListener(e -> login());

        // Remember me checkbox and forgot password link
        Checkbox rememberMe = new Checkbox("Remember me");
        Button forgotPassword = new Button("Forgot password?");
        forgotPassword.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        forgotPassword.getStyle().set("margin-left", "auto");

        HorizontalLayout formActions = new HorizontalLayout(rememberMe, forgotPassword);
        formActions.setWidthFull();
        formActions.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        // Layout for form components
        VerticalLayout formLayout = new VerticalLayout(
            logo, header, username, password, loginButton, formActions
        );
        formLayout.setPadding(false);
        formLayout.setSpacing(true);
        formLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        loginCard.add(formLayout);
        add(loginCard);
    }

    private void login() {
        if ("admin".equals(username.getValue()) && "admin".equals(password.getValue())) {
            getUI().ifPresent(ui -> ui.navigate(""));
        } else {
            Notification notification = Notification.show("Invalid username or password");
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
}
