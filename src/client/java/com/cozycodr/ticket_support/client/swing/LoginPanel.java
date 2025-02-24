package com.cozycodr.ticket_support.client.swing;

import com.cozycodr.ticket_support.client.service.ClientAuthenticationService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import net.miginfocom.swing.MigLayout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.util.function.BiConsumer;

@Component
@Slf4j
public class LoginPanel extends JPanel {
    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final ClientAuthenticationService authService;
    private BiConsumer<String, String> loginHandler;
    private Runnable signupNavigationHandler;

    @Autowired
    public LoginPanel(ClientAuthenticationService authService) {
        this.authService = authService;
        this.usernameField = new JTextField(20);
        this.passwordField = new JPasswordField(20);
    }

    @PostConstruct
    private void initializeUI() {
        setLayout(new MigLayout("fill, insets 0", "[grow]", "[grow]"));
        setBackground(Color.WHITE);
        initializeComponents();
    }

    public void setLoginHandler(BiConsumer<String, String> loginHandler) {
        this.loginHandler = loginHandler;
    }

    public void setSignupNavigationHandler(Runnable signupNavigationHandler) {
        this.signupNavigationHandler = signupNavigationHandler;
    }

    private void initializeComponents() {
        // Create main container with white background
        JPanel container = new JPanel(new MigLayout("wrap, fillx, insets 20", "[grow]", "[]20[]"));
        container.setBackground(Color.WHITE);

        // Add components to container
        container.add(createLogoPanel(), "grow");
        container.add(createLoginFormPanel(), "grow");

        // Add container to center of main panel
        add(container, "center");
    }

    private JPanel createLogoPanel() {
        JPanel panel = new JPanel(new MigLayout("fill, insets 0", "[grow]", "[]10[]"));
        panel.setBackground(Color.WHITE);

        // Logo (placeholder if image not available)
        JLabel logoLabel = new JLabel("IT Support", SwingConstants.CENTER);
        logoLabel.setFont(ApplicationFrame.TITLE_FONT);
        logoLabel.setForeground(ApplicationFrame.PRIMARY_COLOR);

        // Title
        JLabel titleLabel = new JLabel("Ticket System", SwingConstants.CENTER);
        titleLabel.setFont(ApplicationFrame.TITLE_FONT);
        titleLabel.setForeground(ApplicationFrame.PRIMARY_COLOR);

        panel.add(logoLabel, "grow, wrap, align center");
        panel.add(titleLabel, "grow, wrap, align center");

        return panel;
    }

    private JPanel createLoginFormPanel() {
        JPanel panel = new JPanel(new MigLayout("fillx, insets 20", "[right][grow]", "[]10[]10[]20[]"));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230)),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        // Style the input fields
        styleTextField(usernameField);
        styleTextField(passwordField);

        // Add components
        panel.add(new JLabel("Username:"), "right");
        panel.add(usernameField, "growx, wrap");

        panel.add(new JLabel("Password:"), "right");
        panel.add(passwordField, "growx, wrap");

        JButton loginButton = createLoginButton();
        panel.add(loginButton, "skip, growx");

        // signup link
        JPanel signupPanel = createSignupPanel();
        add(signupPanel, "skip, growx");

        return panel;
    }

    private JPanel createSignupPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setOpaque(false);

        JLabel textLabel = new JLabel("Don't have an account?");
        textLabel.setFont(ApplicationFrame.REGULAR_FONT);

        JLabel signupLink = new JLabel("Sign Up");
        signupLink.setFont(ApplicationFrame.REGULAR_FONT);
        signupLink.setForeground(ApplicationFrame.PRIMARY_COLOR);
        signupLink.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add underline on hover
        signupLink.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                signupLink.setText("<html><u>Sign Up</u></html>");
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                signupLink.setText("Sign Up");
            }

            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                signupNavigationHandler.run();
            }
        });

        panel.add(textLabel);
        panel.add(Box.createHorizontalStrut(5));
        panel.add(signupLink);

        return panel;
    }

    private void styleTextField(JTextField field) {
        field.setFont(ApplicationFrame.REGULAR_FONT);
        field.setMargin(new Insets(8, 10, 8, 10));
    }

    private JButton createLoginButton() {
        JButton button = new JButton("Login");
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(ApplicationFrame.PRIMARY_COLOR);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(0, 40));

        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(ApplicationFrame.PRIMARY_COLOR.darker());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(ApplicationFrame.PRIMARY_COLOR);
            }
        });

        // Add action listener
        button.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            loginHandler.accept(username, password);
        });

        return button;
    }

    public void resetFields() {
        usernameField.setText("");
        passwordField.setText("");
        usernameField.requestFocusInWindow();
    }


}
