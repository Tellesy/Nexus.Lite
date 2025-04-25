package ly.neptune.nexus.lite.ui;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinServletRequest;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

@Route(value = "simple-login")
public class SimpleLoginView extends VerticalLayout {

    public SimpleLoginView() {
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        
        // Create purely HTML form for maximum compatibility
        Element loginForm = new Element("form");
        loginForm.setAttribute("method", "post");
        loginForm.setAttribute("action", "login");
        
        Element heading = new Element("h1");
        heading.setText("Nexus Lite Login");
        loginForm.appendChild(heading);
        
        Element userDiv = new Element("div");
        userDiv.getStyle().set("margin-bottom", "15px");
        Element userLabel = new Element("label");
        userLabel.setText("Username: ");
        userLabel.setAttribute("for", "username");
        Element userInput = new Element("input");
        userInput.setAttribute("type", "text");
        userInput.setAttribute("id", "username");
        userInput.setAttribute("name", "username");
        userInput.setAttribute("required", "true");
        userDiv.appendChild(userLabel);
        userDiv.appendChild(userInput);
        
        Element passDiv = new Element("div");
        passDiv.getStyle().set("margin-bottom", "15px");
        Element passLabel = new Element("label");
        passLabel.setText("Password: ");
        passLabel.setAttribute("for", "password");
        Element passInput = new Element("input");
        passInput.setAttribute("type", "password");
        passInput.setAttribute("id", "password");
        passInput.setAttribute("name", "password");
        passInput.setAttribute("required", "true");
        passDiv.appendChild(passLabel);
        passDiv.appendChild(passInput);
        
        Element submitButton = new Element("button");
        submitButton.setAttribute("type", "submit");
        submitButton.setText("Login");
        submitButton.getStyle().set("padding", "8px 16px");
        
        loginForm.appendChild(userDiv);
        loginForm.appendChild(passDiv);
        loginForm.appendChild(submitButton);

        // Add components to the layout
        add(new H1("Nexus Lite"));
        add(new Paragraph("Please log in with username 'admin' and password 'admin'"));
        getElement().appendChild(loginForm);
    }
    
    private void logout() {
        UI.getCurrent().getPage().setLocation("/login");
        SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
        logoutHandler.logout(
                VaadinServletRequest.getCurrent().getHttpServletRequest(), null, null);
    }
}
