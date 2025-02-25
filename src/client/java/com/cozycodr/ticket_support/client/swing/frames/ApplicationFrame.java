package com.cozycodr.ticket_support.client.swing.frames;

import com.cozycodr.ticket_support.client.dto.AuthDataResponse;
import com.cozycodr.ticket_support.client.dto.LoginRequest;
import com.cozycodr.ticket_support.client.service.ClientAuthenticationService;
import com.cozycodr.ticket_support.client.service.EventBusService;
import com.cozycodr.ticket_support.client.swing.panels.auth.LoginPanel;
import com.cozycodr.ticket_support.client.swing.panels.general.MainViewPanel;
import com.cozycodr.ticket_support.client.swing.panels.auth.SignupPanel;
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

    private final ClientAuthenticationService authService;
    private final AuthManager authManager;
    private final EventBusService eventBus;
    private final JLabel clockLabel;

    public static final Color PRIMARY_COLOR = new Color(0, 120, 212);
    public static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 24);
    public static final Font REGULAR_FONT = new Font("Arial", Font.PLAIN, 14);

    @Autowired
    public ApplicationFrame(LoginPanel loginPanel, SignupPanel signupPanel, MainViewPanel mainViewPanel,
                            EventBusService eventBus, ClientAuthenticationService authService, AuthManager authManager) {
        this.loginPanel = loginPanel;
        this.signupPanel = signupPanel;
        this.mainViewPanel = mainViewPanel;
        this.eventBus = eventBus;
        this.authService = authService;
        this.cardLayout = new CardLayout();
        this.mainCardPanel = new JPanel(cardLayout);
        this.clockLabel = new JLabel();
        this.authManager = authManager;

        initializeUI();
        setupClock();
    }

    @PostConstruct
    private void initializeUI() {
        setTitle("IT Support Ticket System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1024, 768));
        setLayout(new MigLayout("fill, insets 0", "[grow]", "[grow]"));

        // Login panel handlers
        loginPanel.setLoginHandler(this::handleLogin);
        loginPanel.setSignupNavigationHandler(() -> cardLayout.show(mainCardPanel, "SIGNUP"));

        // Signup panel handlers
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
                SwingUtilities.invokeLater(() -> {
                    DialogUtils.showErrorDialog(ApplicationFrame.this, message, "Signup Error");
                });
            }
        });
        signupPanel.setLoginNavigationHandler(() -> cardLayout.show(mainCardPanel, "LOGIN"));

        // Add panels to card layout
        mainCardPanel.add(loginPanel, "LOGIN");
        mainCardPanel.add(signupPanel, "SIGNUP");
        mainCardPanel.add(mainViewPanel, "MAIN");

        add(mainCardPanel, "grow, wrap");
        add(clockLabel, "dock south, right, gapright 10");

        cardLayout.show(mainCardPanel, "LOGIN");
        setLocationRelativeTo(null);
    }

    private void handleLogin(String username, String password) {
        LoginRequest request = LoginRequest.builder()
                .username(username)
                .password(password)
                .build();

        authService.login(request,
                response -> SwingUtilities.invokeLater(() -> {
                    // Store the token from the response
                    authManager.setAuthToken(response.getToken());
                    showMainView(response);
                }),
                errorMessage -> SwingUtilities.invokeLater(() -> {
                    DialogUtils.showErrorDialog(ApplicationFrame.this, errorMessage, "Login Error");
                })
        );
    }

    private void showMainView(AuthDataResponse user) {
        currentUser = user;
        mainViewPanel.setCurrentUser(user.getUsername());
        mainViewPanel.setITSupport("IT_SUPPORT".equals(user.getRole()));
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
        // Optionally clear token now that the user logs out
        authManager.setAuthToken(null);
        cardLayout.show(mainCardPanel, "LOGIN");
    }

}