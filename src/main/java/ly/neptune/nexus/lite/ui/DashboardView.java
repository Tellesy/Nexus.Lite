package ly.neptune.nexus.lite.ui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

@Route(value = "", layout = MainLayout.class)
@PageTitle("Dashboard | Nexus Lite")
public class DashboardView extends VerticalLayout {

    private Span databaseStatusBadge;
    private Span fcubsStatusBadge;
    private Button connectButton;
    private boolean isConnected = false;

    public DashboardView() {
        // If ConnectionService is needed, it can be injected here
        setPadding(true);
        setSizeFull();
        setAlignItems(FlexComponent.Alignment.START);

        // Header Section
        H1 title = new H1("Nexus Lite Dashboard");
        title.addClassNames(
            LumoUtility.FontSize.XXXLARGE,
            LumoUtility.Margin.Vertical.MEDIUM
        );
        
        Paragraph subtitle = new Paragraph("Monitor and control your database and FCUBS connections");
        subtitle.addClassNames(
            LumoUtility.TextColor.SECONDARY,
            LumoUtility.Margin.Bottom.LARGE
        );

        // Connection Status Section
        VerticalLayout statusLayout = createStatusSection();
        statusLayout.addClassNames(
            LumoUtility.Background.BASE,
            LumoUtility.BorderRadius.LARGE,
            LumoUtility.BoxShadow.SMALL,
            LumoUtility.Padding.LARGE,
            LumoUtility.Margin.Bottom.LARGE
        );
        statusLayout.setMaxWidth("800px");

        // Connect Button Section with prominent styling
        connectButton = new Button();
        updateConnectButton();
        connectButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE);
        connectButton.addClickListener(e -> toggleConnection());
        connectButton.getStyle().set("margin-top", "20px");

        // Add components to main layout
        add(title, subtitle, statusLayout, connectButton);
    }

    private VerticalLayout createStatusSection() {
        VerticalLayout layout = new VerticalLayout();
        layout.setPadding(false);
        layout.setSpacing(true);

        H3 statusTitle = new H3("Connection Status");
        statusTitle.addClassNames(LumoUtility.Margin.Top.NONE, LumoUtility.Margin.Bottom.MEDIUM);

        // Database Status
        HorizontalLayout dbLayout = new HorizontalLayout();
        dbLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        dbLayout.addClassNames(LumoUtility.Padding.Vertical.SMALL);
        
        Icon dbIcon = VaadinIcon.DATABASE.create();
        dbIcon.setColor("var(--lumo-primary-color)");
        
        Span dbLabel = new Span("Database Connection:");
        dbLabel.addClassNames(LumoUtility.FontWeight.SEMIBOLD);
        
        databaseStatusBadge = createStatusBadge("Disconnected", false);
        
        dbLayout.add(dbIcon, dbLabel, databaseStatusBadge);
        dbLayout.setFlexGrow(1, dbLabel);

        // FCUBS Status
        HorizontalLayout fcubsLayout = new HorizontalLayout();
        fcubsLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        fcubsLayout.addClassNames(LumoUtility.Padding.Vertical.SMALL);
        
        Icon fcubsIcon = VaadinIcon.CLOUD.create();
        fcubsIcon.setColor("var(--lumo-primary-color)");
        
        Span fcubsLabel = new Span("FCUBS Web Service:");
        fcubsLabel.addClassNames(LumoUtility.FontWeight.SEMIBOLD);
        
        fcubsStatusBadge = createStatusBadge("Disconnected", false);
        
        fcubsLayout.add(fcubsIcon, fcubsLabel, fcubsStatusBadge);
        fcubsLayout.setFlexGrow(1, fcubsLabel);
        
        // Hint text
        Paragraph hint = new Paragraph("Configure connection settings in the Configuration view before connecting.");
        hint.addClassNames(
            LumoUtility.TextColor.SECONDARY,
            LumoUtility.FontWeight.MEDIUM,
            LumoUtility.Margin.Top.MEDIUM
        );
        hint.getStyle().set("font-style", "italic");

        layout.add(statusTitle, dbLayout, fcubsLayout, hint);
        return layout;
    }

    private Span createStatusBadge(String text, boolean isConnected) {
        Span badge = new Span(text);
        badge.getElement().getThemeList().add("badge");
        
        if (isConnected) {
            badge.getElement().getThemeList().add("success");
        } else {
            badge.getElement().getThemeList().add("error");
        }
        
        return badge;
    }

    private void toggleConnection() {
        isConnected = !isConnected;
        
        if (isConnected) {
            // Simulate connection attempt
            Notification.show("Connecting to database and FCUBS services...", 3000, Notification.Position.MIDDLE);
            
            // Update status badges
            databaseStatusBadge.setText("Connected");
            databaseStatusBadge.getElement().getThemeList().remove("error");
            databaseStatusBadge.getElement().getThemeList().add("success");
            
            fcubsStatusBadge.setText("Connected");
            fcubsStatusBadge.getElement().getThemeList().remove("error");
            fcubsStatusBadge.getElement().getThemeList().add("success");
            
            // Show success notification
            Notification notification = Notification.show("Successfully connected to services");
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } else {
            // Simulate disconnection
            databaseStatusBadge.setText("Disconnected");
            databaseStatusBadge.getElement().getThemeList().remove("success");
            databaseStatusBadge.getElement().getThemeList().add("error");
            
            fcubsStatusBadge.setText("Disconnected");
            fcubsStatusBadge.getElement().getThemeList().remove("success");
            fcubsStatusBadge.getElement().getThemeList().add("error");
            
            // Show notification
            Notification notification = Notification.show("Disconnected from services");
            notification.addThemeVariants(NotificationVariant.LUMO_CONTRAST);
        }
        
        updateConnectButton();
    }
    
    private void updateConnectButton() {
        if (isConnected) {
            connectButton.setText("Disconnect");
            connectButton.setIcon(new Icon(VaadinIcon.POWER_OFF));
            connectButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
            connectButton.removeThemeVariants(ButtonVariant.LUMO_PRIMARY);
        } else {
            connectButton.setText("Connect");
            connectButton.setIcon(new Icon(VaadinIcon.CONNECT));
            connectButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            connectButton.removeThemeVariants(ButtonVariant.LUMO_ERROR);
        }
    }
}
