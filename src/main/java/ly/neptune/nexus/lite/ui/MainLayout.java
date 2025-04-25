package ly.neptune.nexus.lite.ui;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.VaadinServletRequest;
import ly.neptune.nexus.lite.ui.DashboardView;
import ly.neptune.nexus.lite.ui.ConfigurationView;

public class MainLayout extends AppLayout {
    public MainLayout() {
        createHeader();
        createDrawer();
    }

    private void createHeader() {
        // Logo
        Image logo = new Image("/images/logo.svg", "Nexus Lite Logo");
        logo.setHeight("44px");

        // Title
        H1 title = new H1("Nexus Lite");
        title.getStyle().set("font-size", "var(--lumo-font-size-l)")
             .set("margin", "0");

        // Header layout
        HorizontalLayout header = new HorizontalLayout(new DrawerToggle(), logo, title);
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.expand(title);
        header.setWidthFull();
        header.addClassNames("py-0", "px-m");

        // User menu (right side)
        Button logout = new Button("Logout", e -> logout());
        header.add(logout);

        addToNavbar(header);
    }

    private void createDrawer() {
        // Create drawer items
        RouterLink dashboardLink = new RouterLink("Dashboard", DashboardView.class);

        RouterLink configLink = new RouterLink("Configuration", ConfigurationView.class);

        // Layout for drawer items
        addToDrawer(new VerticalLayout(dashboardLink, configLink));
    }

    private void logout() {
        // Get the current request to build the logout URL
        VaadinServletRequest request = VaadinServletRequest.getCurrent();
        String logoutUrl = request.getContextPath() + "/logout";
        // Redirect to the Spring Security logout URL
        getUI().ifPresent(ui -> ui.getPage().setLocation(logoutUrl));
    }
}
