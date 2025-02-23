package com.cozycodr.ticket_support.client.swing;

import lombok.extern.slf4j.Slf4j;
import net.miginfocom.swing.MigLayout;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Slf4j
public class ApplicationFrame extends JFrame {
    private final CardLayout cardLayout;
    private final JPanel mainCardPanel;
    private final LoginPanel loginPanel;
    private final SignupPanel signupPanel;
    private final MainPanel mainPanel;
    private final JLabel clockLabel;
    private Timer clockTimer;

    // Shared UI constants
    public static final Color PRIMARY_COLOR = new Color(0, 120, 212);
    public static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 24);
    public static final Font REGULAR_FONT = new Font("Arial", Font.PLAIN, 14);


    public ApplicationFrame() {
        // Basic frame setup
        setTitle("IT Support Ticket System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1024, 768));

        // Initialize components
        cardLayout = new CardLayout();
        mainCardPanel = new JPanel(cardLayout);
        clockLabel = new JLabel();
        clockLabel.setFont(new Font("Monospace", Font.PLAIN, 12));
        clockLabel.setForeground(Color.GRAY);

        // Create panels
        loginPanel = new LoginPanel(
                this::handleLogin,
                () -> cardLayout.show(mainCardPanel, "SIGNUP") // Add navigation to signup
        );

        signupPanel = new SignupPanel(new SignupPanel.SignupCallback() {
            @Override
            public void onSignupSuccess(String token) {
                // TODO: Store token and handle user session
                showMainPanel(signupPanel.getUsername());
            }

            @Override
            public void onSignupError(String message) {
                JOptionPane.showMessageDialog(
                        ApplicationFrame.this,
                        message,
                        "Signup Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }, () -> cardLayout.show(mainCardPanel, "LOGIN"));

        mainPanel = new MainPanel();

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

    private void handleLogin(String username, String password) {
        // TODO: Implement actual authentication logic
        if ("cozyCodr".equals(username) && "password".equals(password)) {
            showMainPanel(username);
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Invalid username or password",
                    "Login Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void showLoginPanel() {
        loginPanel.resetFields();
        cardLayout.show(mainCardPanel, "LOGIN");
    }

    private void showSignupPanel() {
        signupPanel.clearForm(); // Add this method to SignupPanel if not already present
        cardLayout.show(mainCardPanel, "SIGNUP");
    }

    private void showMainPanel(String username) {
        mainPanel.setCurrentUser(username);
        cardLayout.show(mainCardPanel, "MAIN");
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

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            ApplicationFrame frame = new ApplicationFrame();
            frame.setVisible(true);
        });
    }
}