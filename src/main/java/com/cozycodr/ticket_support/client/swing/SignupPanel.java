package com.cozycodr.ticket_support.client.swing;

import com.cozycodr.ticket_support.client.service.AuthenticationService;
import com.cozycodr.ticket_support.model.dto.RegistrationRequest;
import com.cozycodr.ticket_support.model.enums.Role;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class SignupPanel extends JPanel {

    private final JTextField firstNameField;
    private final JTextField lastNameField;
    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final JPasswordField confirmPasswordField;
    private final JComboBox<Role> roleComboBox;
    private final AuthenticationService authService;
    private final SignupCallback callback;
    private final Runnable loginNavigationHandler;


    public interface SignupCallback {
        void onSignupSuccess(String token);
        void onSignupError(String message);
    }

    public SignupPanel(SignupCallback callback, Runnable loginNavigationHandler){
        this.callback = callback;
        this.authService = new AuthenticationService();
        this.loginNavigationHandler = loginNavigationHandler;

        // Initializing components
        firstNameField = new JTextField(20);
        lastNameField = new JTextField(20);
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        confirmPasswordField = new JPasswordField(20);
        roleComboBox = new JComboBox<>(Role.values());

        setupUI();
    }

    private void setupUI(){
        setLayout(new MigLayout("fillx, insets 20", "[right][grow]", "[]10[]10[]10[]10[]10[]20[]"));
        setBackground(Color.WHITE);

        // Header
        add(createHeaderLabel("Create Account"), "span 2, center, wrap");

        // Form Fields
        add(createLabel("First Name:"), "right");
        add(firstNameField, "growx, wrap");

        add(createLabel("Last Name:"), "right");
        add(lastNameField, "growx, wrap");

        add(createLabel("Username:"), "right");
        add(usernameField, "growx, wrap");

        add(createLabel("Password:"), "right");
        add(passwordField, "growx, wrap");

        add(createLabel("Confirm Password:"), "right");
        add(confirmPasswordField, "growx, wrap");

        add(createLabel("Role:"), "right");
        add(roleComboBox, "growx, wrap");

        // signup button
        JButton signupButton = createSignupButton();
        add(signupButton, "skip, growx");

        // Add login link
        JPanel loginPanel = createLoginPanel();
        add(loginPanel, "skip, growx");

        // Style components
        styleComponents();
    }
    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setOpaque(false);

        JLabel textLabel = new JLabel("Already have an account?");
        textLabel.setFont(ApplicationFrame.REGULAR_FONT);

        JLabel loginLink = new JLabel("Login");
        loginLink.setFont(ApplicationFrame.REGULAR_FONT);
        loginLink.setForeground(ApplicationFrame.PRIMARY_COLOR);
        loginLink.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add underline on hover
        loginLink.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                loginLink.setText("<html><u>Login</u></html>");
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                loginLink.setText("Login");
            }

            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cardLayout.show(mainCardPanel, "LOGIN");
            }
        });

        panel.add(textLabel);
        panel.add(Box.createHorizontalStrut(5));
        panel.add(loginLink);

        return panel;
    }

    private JLabel createHeaderLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 24));
        label.setForeground(new Color(0, 120, 212));
        return label;
    }

    private JLabel createLabel(String text){
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        return label;
    }

    private JButton createSignupButton() {
        JButton button = new JButton("Sign Up");
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(0, 120, 212));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(0, 40));

        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0, 100, 180));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0, 120, 212));
            }
        });

        button.addActionListener(e -> handleSignup());
        return button;
    }

    private void styleComponents() {
        // Style text fields
        Component[] components = {firstNameField, lastNameField, usernameField,
                passwordField, confirmPasswordField, roleComboBox};

        for (Component comp : components) {
            if (comp instanceof JTextField) {
                JTextField field = (JTextField) comp;
                field.setFont(new Font("Arial", Font.PLAIN, 14));
                field.setMargin(new Insets(8, 10, 8, 10));
            }
        }

        // Style combo box
        roleComboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        roleComboBox.setBackground(Color.WHITE);
    }

    private void handleSignup() {
        // Validate input
        if (!validateInput()) {
            return;
        }

        try {
            RegistrationRequest request = RegistrationRequest.builder()
                    .firstName(firstNameField.getText().trim())
                    .lastName(lastNameField.getText().trim())
                    .username(usernameField.getText().trim())
                    .password(new String(passwordField.getPassword()))
                    .role((Role) roleComboBox.getSelectedItem())
                    .build();

            // Call authentication service
            authService.register(request,
                    response -> {
                        clearForm();
                        callback.onSignupSuccess(response.getToken());
                    },
                    callback::onSignupError
            );

        } catch (Exception ex) {
            callback.onSignupError("Error during signup: " + ex.getMessage());
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "Validation Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    private boolean validateInput() {
        // Validate first name
        if (firstNameField.getText().trim().isEmpty()) {
            showError("First name is required");
            return false;
        }

        // Validate last name
        if (lastNameField.getText().trim().isEmpty()) {
            showError("Last name is required");
            return false;
        }

        // Validate username
        if (usernameField.getText().trim().isEmpty()) {
            showError("Username is required");
            return false;
        }

        // Validate password
        if (passwordField.getPassword().length < 6) {
            showError("Password must be at least 6 characters long");
            return false;
        }

        // Validate password confirmation
        if (!Arrays.equals(passwordField.getPassword(), confirmPasswordField.getPassword())) {
            showError("Passwords do not match");
            return false;
        }

        return true;
    }

    // Method to get username after successful signup
    public String getUsername() {
        return usernameField.getText().trim();
    }

    void clearForm() {
        firstNameField.setText("");
        lastNameField.setText("");
        usernameField.setText("");
        passwordField.setText("");
        confirmPasswordField.setText("");
        roleComboBox.setSelectedIndex(0);
        firstNameField.requestFocusInWindow();
    }

}
