package com.cozycodr.ticket_support.client.swing;



import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class MainPanel extends JPanel {
    private final JPanel sidebarPanel;
    private final JPanel contentPanel;
    private final JPanel headerPanel;
    private final JLabel userLabel;
    private boolean isITSupport = false;

    private static final Color PRIMARY_COLOR = new Color(0, 120, 212);

    public MainPanel() {
        setLayout(new MigLayout("fill, insets 0", "[200!][grow]", "[60!][grow]"));
        setBackground(Color.WHITE);

        // Initialize panels
        headerPanel = createHeaderPanel();
        sidebarPanel = createSidebarPanel();
        contentPanel = createContentPanel();

        // Store user label for later updates
        userLabel = new JLabel();
        userLabel.setForeground(Color.WHITE);
        headerPanel.add(userLabel, "right");

        // Add panels to main layout
        add(headerPanel, "span 2, growx, wrap");
        add(sidebarPanel, "growy");
        add(contentPanel, "grow");
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new MigLayout("fillx, insets 10", "[grow][]"));
        panel.setBackground(new Color(51, 51, 51));

        JLabel titleLabel = new JLabel("IT Support Ticket System");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));

        panel.add(titleLabel, "left");

        return panel;
    }

    private JPanel createSidebarPanel() {
        JPanel panel = new JPanel(new MigLayout("fillx, insets 0", "[grow]"));
        panel.setBackground(new Color(242, 242, 242));

        addNavigationButton(panel, "Dashboard");
        addNavigationButton(panel, "Create Ticket");
        addNavigationButton(panel, "My Tickets");

        return panel;
    }

    private JPanel createContentPanel() {
        JPanel panel = new JPanel(new MigLayout("fill, insets 10", "[grow]", "[grow]"));
        panel.setBackground(Color.WHITE);
        return panel;
    }

    private void addNavigationButton(JPanel panel, String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setPreferredSize(new Dimension(180, 40));
        button.setBackground(new Color(242, 242, 242));
        button.setBorderPainted(false);

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(230, 230, 230));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(242, 242, 242));
            }
        });

        panel.add(button, "wrap, growx");
    }

    public void setCurrentUser(String username) {
        userLabel.setText("Welcome, " + username);
        // Update UI based on user role if needed
        updateUIForRole();
    }

    private void updateUIForRole() {
        if (isITSupport) {
            addNavigationButton(sidebarPanel, "All Tickets");
            addNavigationButton(sidebarPanel, "Reports");
        }
        revalidate();
        repaint();
    }
}
