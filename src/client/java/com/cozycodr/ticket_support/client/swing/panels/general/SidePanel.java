package com.cozycodr.ticket_support.client.swing.panels.general;

import com.cozycodr.ticket_support.client.event.ApplicationEvent;
import com.cozycodr.ticket_support.client.service.EventBusService;
import net.miginfocom.swing.MigLayout;
import org.springframework.beans.factory.annotation.Autowired;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@org.springframework.stereotype.Component
public class SidePanel extends JPanel {
    private final Map<String, JButton> navigationButtons = new HashMap<>();
    private Consumer<String> navigationCallback;
    private final EventBusService eventBus;
    private String currentRole;

    @Autowired
    public SidePanel(EventBusService eventBus) {
        this.eventBus = eventBus;
        initializePanel();
    }

    private void initializePanel() {
        setLayout(new MigLayout("fillx, insets 10", "[grow]", "[]20[]10[]10[]push[]"));
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(250, 0));
        setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(220, 220, 220)));

        // Header label
        JLabel headerLabel = new JLabel("Ticket Tracking System");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(headerLabel, "wrap, align center, gapbottom 20");

        // Create navigation buttons with correct identifiers
        addNavigationButton("MyTickets", "Tickets", true);
        // Create Ticket button will be added when role is set

        // Add logout button at the bottom
        JButton logoutButton = createLogoutButton();
        add(logoutButton, "growx, wrap, gapbottom 10");
    }

    private JButton createLogoutButton() {
        JButton logoutButton = new JButton("\u2192 Logout"); // Unicode right arrow
        logoutButton.setFocusPainted(false);
        logoutButton.setFont(new Font("Arial", Font.BOLD, 14));
        logoutButton.setHorizontalAlignment(SwingConstants.LEFT);
        logoutButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        logoutButton.setBackground(new Color(255, 232, 232));
        logoutButton.setForeground(new Color(220, 53, 69));
        logoutButton.setBorderPainted(false);
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add hover effect
        logoutButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                logoutButton.setBackground(new Color(255, 218, 218));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                logoutButton.setBackground(new Color(255, 232, 232));
            }
        });

        // Add logout action
        logoutButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to logout?",
                    "Confirm Logout",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );

            if (confirm == JOptionPane.YES_OPTION) {
                eventBus.publish(new ApplicationEvent(ApplicationEvent.LOGOUT, null));
            }
        });

        return logoutButton;
    }

    public void setOnNavigationSelected(Consumer<String> callback) {
        this.navigationCallback = callback;
    }

    private void addNavigationButton(String navId, String displayText, boolean isActive) {
        JButton button = createNavButton(displayText, isActive);
        navigationButtons.put(navId, button);

        button.addActionListener(e -> {
            if (navigationCallback != null) {
                // Update visual state of buttons
                navigationButtons.values().forEach(btn ->
                        btn.setBackground(Color.WHITE));
                button.setBackground(new Color(230, 230, 230));

                // Call the navigation callback with the correct identifier
                navigationCallback.accept(navId);
            }
        });

        add(button, "growx, wrap");
    }

    public void setUserRole(String role) {
        this.currentRole = role;
        updateNavigationButtons();
    }

    private void updateNavigationButtons() {
        // Clear existing buttons except "MyTickets"
        Component[] components = getComponents();
        for (Component component : components) {
            if (component instanceof JButton &&
                    !((JButton) component).getText().equals("Tickets") &&
                    !((JButton) component).getText().contains("Logout")) {
                remove(component);
            }
        }

        // Keep MyTickets button as first navigation item
        navigationButtons.get("MyTickets").setBackground(new Color(230, 230, 230));

        // Add Create Ticket button only for EMPLOYEE role
        if ("EMPLOYEE".equals(currentRole)) {
            // Remove existing Create Ticket button if it exists
            navigationButtons.remove("CreateTicket");

            // Add the Create Ticket button after MyTickets
            addNavigationButton("CreateTicket", "Create Ticket", false);
        }

        // Ensure logout button stays at bottom
        Component logoutButton = components[components.length - 1];
        remove(logoutButton);
        add(logoutButton, "growx, wrap, gapbottom 10");

        // Update the panel
        revalidate();
        repaint();
    }

    private JButton createNavButton(String text, boolean active) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        button.setBackground(active ? new Color(230, 230, 230) : Color.WHITE);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            private Color originalBackground;

            @Override
            public void mouseEntered(MouseEvent e) {
                originalBackground = button.getBackground();
                if (!originalBackground.equals(new Color(230, 230, 230))) {
                    button.setBackground(new Color(245, 245, 245));
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(originalBackground);
            }
        });

        return button;
    }

    public void setActiveButton(String navId) {
        navigationButtons.forEach((key, button) -> {
            button.setBackground(key.equals(navId) ?
                    new Color(230, 230, 230) : Color.WHITE);
        });
    }
}