package com.cozycodr.ticket_support.client.swing;

import com.cozycodr.ticket_support.client.dto.AuthDataResponse;
import com.cozycodr.ticket_support.client.dto.LoginRequest;
import com.cozycodr.ticket_support.client.event.ApplicationEvent;
import com.cozycodr.ticket_support.client.service.ClientAuthenticationService;
import com.cozycodr.ticket_support.client.service.EventBusService;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.miginfocom.swing.MigLayout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Slf4j
@Component
public class ApplicationFrame extends JFrame {

    // Method to get current user
    @Getter
    private AuthDataResponse currentUser;
    private final CardLayout cardLayout;
    private final JPanel mainCardPanel;
    private final LoginPanel loginPanel;
    private final SignupPanel signupPanel;
    private MainPanel mainPanel;
    private final StatusBarPanel statusBarPanel;
    private final JLabel clockLabel;
    private Timer clockTimer;
    private final EventBusService eventBus;
    private final ClientAuthenticationService authService;

    // Shared UI constants
    public static final Color PRIMARY_COLOR = new Color(0, 120, 212);
    public static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 24);
    public static final Font REGULAR_FONT = new Font("Arial", Font.PLAIN, 14);


    @Autowired
    public ApplicationFrame(
            LoginPanel loginPanel, SignupPanel signupPanel, MainPanel mainPanel, EventBusService eventBus,
            StatusBarPanel statusBarPanel, ClientAuthenticationService authService) {
        this.loginPanel = loginPanel;
        this.signupPanel = signupPanel;
        this.mainPanel = mainPanel;
        this.statusBarPanel = statusBarPanel;
        this.authService = authService;
        this.eventBus = eventBus;

        // Initialize layout components
        this.cardLayout = new CardLayout();
        this.mainCardPanel = new JPanel(cardLayout);
        this.clockLabel = new JLabel();

        initializeUI();
        setupEventListeners();
    }

    @PostConstruct
    private void initializeUI() {
        // Basic frame setup
        setTitle("IT Support Ticket System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1024, 768));

        clockLabel.setFont(new Font("Monospace", Font.PLAIN, 12));
        clockLabel.setForeground(Color.GRAY);

        // Configure login panel
        loginPanel.setLoginHandler(this::handleLogin);
        loginPanel.setSignupNavigationHandler(() -> cardLayout.show(mainCardPanel, "SIGNUP"));

        // Configure signup panel
        signupPanel.setCallback(new SignupPanel.SignupCallback() {
            @Override
            public void onSignupSuccess(AuthDataResponse authResponse) {
                SwingUtilities.invokeLater(() -> {
                    currentUser = authResponse;
                    log.info(
                            authResponse.getUsername(),
                            "signup",
                            String.format(
                                    "signup: New user registered: %s (%s)",
                                    authResponse.getUsername(),
                                    authResponse.getRole()
                            )
                    );
                    updateStatusBar(authResponse);
                    showMainPanel(authResponse);
                });
            }

            @Override
            public void onSignupError(String message) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(
                            ApplicationFrame.this,
                            message,
                            "Signup Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                });
            }
        });
        signupPanel.setLoginNavigationHandler(() -> cardLayout.show(mainCardPanel, "LOGIN"));

        // Set up the main layout
        setLayout(new MigLayout("fill, insets 0", "[grow]", "[grow][]"));

        // Add panels to card layout
        mainCardPanel.add(loginPanel, "LOGIN");
        mainCardPanel.add(signupPanel, "SIGNUP");
        mainCardPanel.add(mainPanel, "MAIN");

        // Add components to frame
        add(mainCardPanel, "grow, wrap");
        add(clockLabel, "right, gapright 10");

        // Start clock and show login
        startClock();
        showLoginPanel();

        // Center on screen
        setLocationRelativeTo(null);
    }

    private void setupEventListeners() {
        eventBus.subscribe(ApplicationEvent.LOGOUT, event -> {
            SwingUtilities.invokeLater(this::handleLogout);
        });
    }

    private void handleLogout() {
        String username = currentUser != null ? currentUser.getUsername() : "Unknown";
        this.currentUser = null;
        statusBarPanel.setCurrentUser("Not logged in");
        showLoginPanel();
    }

    private void updateStatusBar(AuthDataResponse user) {
        statusBarPanel.setCurrentUser(user.getUsername());
        statusBarPanel.setCurrentTime(
                LocalDateTime.now(ZoneOffset.UTC)
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        );
    }

    private void handleLogin(String username, String password) {
        LoginRequest request = LoginRequest.builder()
                .username(username)
                .password(password)
                .build();

        authService.login(request,
                response -> {
                    SwingUtilities.invokeLater(() -> {
                        this.currentUser = response;
                        showMainPanel(response);
                    });
                },
                errorMessage -> SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(
                            this,
                            errorMessage,
                            "Login Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                })
        );
    }

    private void showLoginPanel() {
        loginPanel.resetFields();
        cardLayout.show(mainCardPanel, "LOGIN");
    }

    private void showSignupPanel() {
        signupPanel.clearForm(); // Add this method to SignupPanel if not already present
        cardLayout.show(mainCardPanel, "SIGNUP");
    }

    private void showMainPanel(AuthDataResponse user) {
        this.currentUser = user;
        mainPanel.setCurrentUser(user.getUsername());
        mainPanel.setITSupport("IT_SUPPORT".equals(user.getRole()));
        updateStatusBar(user);
        cardLayout.show(mainCardPanel, "MAIN");
    }

    // Add logout method
    public void logout() {
        String username = currentUser != null ? currentUser.getUsername() : "Unknown";
        this.currentUser = null;
        log.info("{} has logged out at {}", username, new Date().getTime());
        statusBarPanel.setCurrentUser("Not logged in");
        showLoginPanel();
    }

    private void startClock() {
        clockTimer = new Timer(1000, e -> updateClock());
        clockTimer.start();
        updateClock();
    }

    private void updateClock() {
        LocalDateTime utcNow = LocalDateTime.now(ZoneOffset.UTC);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        clockLabel.setText("UTC: " + utcNow.format(formatter));
    }
}