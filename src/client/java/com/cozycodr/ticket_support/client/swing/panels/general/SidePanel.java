package com.cozycodr.ticket_support.client.swing.panels.general;

import net.miginfocom.swing.MigLayout;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@Component
public class SidePanel extends JPanel {
    private final Map<String, JButton> navigationButtons = new HashMap<>();
    private Consumer<String> navigationCallback;

    public SidePanel() {
        initializePanel();
    }

    private void initializePanel() {
        setLayout(new MigLayout("fillx, insets 10", "[grow]", "[]10[]10[]"));
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(250, 0));
        setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(220, 220, 220)));

        // Header label
        JLabel headerLabel = new JLabel("Ticket Tracking System");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(headerLabel, "wrap, align center, gapbottom 20");

        // Create navigation buttons with correct identifiers
        addNavigationButton("MyTickets", "My Tickets", true);
        addNavigationButton("CreateTicket", "Create Ticket", false);
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