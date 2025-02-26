package com.cozycodr.ticket_support.client.swing.frames;

import com.cozycodr.ticket_support.client.dto.AuthDataResponse;
import com.cozycodr.ticket_support.client.dto.LoginRequest;
import com.cozycodr.ticket_support.client.event.ApplicationEvent;
import com.cozycodr.ticket_support.client.service.ClientAuthenticationService;
import com.cozycodr.ticket_support.client.service.EventBusService;
import com.cozycodr.ticket_support.client.swing.panels.auth.LoginPanel;
import com.cozycodr.ticket_support.client.swing.panels.auth.SignupPanel;
import com.cozycodr.ticket_support.client.swing.panels.general.MainViewPanel;
import com.cozycodr.ticket_support.client.swing.panels.ticket.MyTicketsPanel;
import com.cozycodr.ticket_support.client.utils.AuthManager;
import com.cozycodr.ticket_support.client.utils.DialogUtils;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import net.miginfocom.swing.MigLayout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Component
public class ApplicationFrame extends JFrame {

    @Getter
    private AuthDataResponse currentUser;
    private final CardLayout cardLayout;
    private final JPanel mainCardPanel;
    private final LoginPanel loginPanel;
    private final SignupPanel signupPanel;
    private final MainViewPanel mainViewPanel;
    private final MyTicketsPanel myTicketsPanel;
    private final ClientAuthenticationService authService;
    private final AuthManager authManager;
    private final EventBusService eventBus;
    private final JLabel clockLabel;

    @Autowired
    public ApplicationFrame(LoginPanel loginPanel, SignupPanel signupPanel, MainViewPanel mainViewPanel,
                            EventBusService eventBus, ClientAuthenticationService authService,
                            AuthManager authManager, MyTicketsPanel myTicketsPanel
    ) {
        this.loginPanel = loginPanel;
        this.signupPanel = signupPanel;
        this.mainViewPanel = mainViewPanel;
        this.myTicketsPanel = myTicketsPanel;
        this.eventBus = eventBus;
        this.authService = authService;
        this.cardLayout = new CardLayout();
        this.mainCardPanel = new JPanel(cardLayout);
        this.clockLabel = new JLabel();
        this.authManager = authManager;
        initializeUI();
        setupClock();
        setupEventListeners();
    }

    private void setupEventListeners() {
        // Subscribe to logout events
        eventBus.subscribe(ApplicationEvent.LOGOUT, event -> {
            SwingUtilities.invokeLater(this::logout);
        });
    }


    @PostConstruct
    private void initializeUI() {
        setTitle("IT Support Ticket System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1024, 768));
        setLayout(new MigLayout("fill, insets 0", "[grow]", "[grow]"));

        // Configure login/signup panels.
        loginPanel.setLoginHandler(this::handleLogin);
        loginPanel.setSignupNavigationHandler(() -> cardLayout.show(mainCardPanel, "SIGNUP"));
        signupPanel.setCallback(new SignupPanel.SignupCallback() {
            @Override
            public void onSignupSuccess(AuthDataResponse authResponse) {
                SwingUtilities.invokeLater(() -> {
                    currentUser = authResponse;
                    showMainView(authResponse);
                });
            }
            @Override
            public void onSignupError(String message) {
                SwingUtilities.invokeLater(() ->
                        DialogUtils.showErrorDialog(ApplicationFrame.this, message, "Signup Error"));
            }
        });
        signupPanel.setLoginNavigationHandler(() -> cardLayout.show(mainCardPanel, "LOGIN"));

        mainCardPanel.add(loginPanel, "LOGIN");
        mainCardPanel.add(signupPanel, "SIGNUP");
        mainCardPanel.add(mainViewPanel, "MAIN");

        add(mainCardPanel, "grow, wrap");
        add(clockLabel, "dock south, right, gapright 10");

        cardLayout.show(mainCardPanel, "LOGIN");
        setLocationRelativeTo(null);
        pack();
        setVisible(true);
    }

    private void handleLogin(String username, String password) {
        LoginRequest request = LoginRequest.builder()
                .username(username)
                .password(password)
                .build();

        authService.login(request,
                response -> SwingUtilities.invokeLater(() -> {
                    authManager.setAuthToken(response.getToken());
                    this.currentUser = response;

                    // Set the user context before showing main view
                    myTicketsPanel.setCurrentUser(response);
                    mainViewPanel.setCurrentUser(response.getUsername());
                    mainViewPanel.updateUserRole(response.getRole());

                    // Show main view
                    cardLayout.show(mainCardPanel, "MAIN");
                }),
                errorMessage -> SwingUtilities.invokeLater(() ->
                        DialogUtils.showErrorDialog(this, errorMessage, "Login Error"))
        );
    }
    private void showMainView(AuthDataResponse user) {
        currentUser = user;
        mainViewPanel.setCurrentUser(user.getUsername());
        mainViewPanel.updateUserRole(user.getRole());
        cardLayout.show(mainCardPanel, "MAIN");
    }

    private void setupClock() {
        clockLabel.setFont(new Font("Monospaced", Font.PLAIN, 12));
        clockLabel.setForeground(Color.GRAY);
        Timer clockTimer = new Timer(1000, e -> {
            LocalDateTime utcNow = LocalDateTime.now(ZoneOffset.UTC);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            clockLabel.setText("UTC: " + utcNow.format(formatter));
        });
        clockTimer.start();
    }

    public void logout() {
        currentUser = null;
        authManager.setAuthToken(null);
        cardLayout.show(mainCardPanel, "LOGIN");
        loginPanel.resetFields(); // Add this method to LoginPanel if not exists

        // Reset any necessary state in the panels
        mainViewPanel.reset(); // Add this method to MainViewPanel if needed

        revalidate();
        repaint();
    }
}