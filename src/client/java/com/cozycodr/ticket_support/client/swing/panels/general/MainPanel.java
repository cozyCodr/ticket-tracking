package com.cozycodr.ticket_support.client.swing.panels.general;

import com.cozycodr.ticket_support.client.event.ApplicationEvent;
import com.cozycodr.ticket_support.client.service.EventBusService;
import com.cozycodr.ticket_support.client.swing.panels.ticket.CreateTicketPanel;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import net.miginfocom.swing.MigLayout;
import org.springframework.beans.factory.annotation.Autowired;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@org.springframework.stereotype.Component
public class MainPanel extends JPanel {
    private final JPanel contentPanel;
    private final JLabel userLabel;
    private final Map<String, JButton> navigationButtons;
    private boolean isITSupport = false;
    private final EventBusService eventBus;

    @Autowired
    public MainPanel(EventBusService eventBus) {
        this.eventBus = eventBus;
        this.contentPanel = new JPanel();
        this.userLabel = new JLabel();
        this.navigationButtons = new HashMap<>();
        initializeUI();
    }

    //
    @PostConstruct
    private void initializeUI() {
        setLayout(new MigLayout("fill, insets 0", "[200!][grow]", "[50!][grow]"));
        setBackground(Color.WHITE);

        // Create components
        JPanel headerPanel = createHeaderPanel();
        JPanel sidebarPanel = createSidebarPanel();
        configureContentPanel();

        // Add components with proper constraints
        add(headerPanel, "span 2, growx, wrap");
        add(sidebarPanel, "growy");
        add(contentPanel, "grow");
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new MigLayout("fill, insets 8", "[]push[][]", "[]"));
        headerPanel.setBackground(new Color(51, 51, 51));

        JLabel titleLabel = new JLabel("IT Support Ticket System");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));

        userLabel.setForeground(Color.WHITE);
        userLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        JButton logoutButton = new JButton("Logout");
        styleLogoutButton(logoutButton);

        headerPanel.add(titleLabel);
        headerPanel.add(userLabel);
        headerPanel.add(logoutButton);

        return headerPanel;
    }

    private JPanel createSidebarPanel() {
        JPanel sidebar = new JPanel(new MigLayout("fillx, insets 0", "[grow]", "[]0[]0[]0[]"));
        sidebar.setBackground(new Color(242, 242, 242));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(220, 220, 220)));

        // Add navigation buttons
        addNavigationButton(sidebar, "Dashboard");
        addNavigationButton(sidebar, "Create Ticket");
        addNavigationButton(sidebar, "My Tickets");

        return sidebar;
    }

    private void styleLogoutButton(JButton button) {
        button.setFont(new Font("Arial", Font.PLAIN, 12));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(51, 51, 51));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(70, 70, 70));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(51, 51, 51));
            }
        });

        button.addActionListener(e -> {
            log.info(
                    userLabel.getText().replace("Welcome, ", ""),
                    "logout",
                    "User initiated logout"
            );
            eventBus.publish(new ApplicationEvent(ApplicationEvent.LOGOUT, null));
        });
    }

    private void styleNavigationButton(JButton button) {
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setPreferredSize(new Dimension(200, 45));
        button.setBackground(new Color(242, 242, 242));
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setIconTextGap(15);
        button.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(new Color(230, 230, 230));
            }

            @Override
            public void mouseExited(MouseEvent evt) {
                button.setBackground(new Color(242, 242, 242));
            }
        });
    }


    private void configureContentPanel() {
        contentPanel.setLayout(new MigLayout("fill, insets 10", "[grow]", "[grow]"));
        contentPanel.setBackground(Color.WHITE);

        // Add initial content (e.g., dashboard)
        showDashboard();
    }

    private void addNavigationButton(JPanel panel, String text) {
        if (navigationButtons.containsKey(text)) {
            return; // Prevent duplicate buttons
        }

        JButton button = new JButton(text);
        styleNavigationButton(button);

        button.addActionListener(e -> {
            log.info(
                    userLabel.getText().replace("Welcome, ", ""),
                    "navigation",
                    "Navigated to " + text
            );
            handleNavigation(text);
        });

        panel.add(button, "wrap, growx");
        navigationButtons.put(text, button);
    }

    private void handleNavigation(String destination) {
        contentPanel.removeAll();

        switch (destination) {
            case "Dashboard" -> showDashboard();
            case "Create Ticket" -> showCreateTicket();
            case "My Tickets" -> showMyTickets();
            case "All Tickets" -> showAllTickets();
            case "Reports" -> showReports();
        }

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showDashboard() {
        // TODO: Implement dashboard view
        JLabel placeholder = new JLabel("Dashboard Content");
        placeholder.setHorizontalAlignment(SwingConstants.CENTER);
        contentPanel.add(placeholder, "grow");
    }

    private void showCreateTicket() {
        contentPanel.add(new CreateTicketPanel(), "grow");
    }

    private void showMyTickets() {
        // TODO: Implement my tickets view
        JLabel placeholder = new JLabel("My Tickets Content");
        placeholder.setHorizontalAlignment(SwingConstants.CENTER);
        contentPanel.add(placeholder, "grow");
    }

    private void showAllTickets() {
        // TODO: Implement all tickets view
        JLabel placeholder = new JLabel("All Tickets Content");
        placeholder.setHorizontalAlignment(SwingConstants.CENTER);
        contentPanel.add(placeholder, "grow");
    }

    private void showReports() {
        // TODO: Implement reports view
        JLabel placeholder = new JLabel("Reports Content");
        placeholder.setHorizontalAlignment(SwingConstants.CENTER);
        contentPanel.add(placeholder, "grow");
    }

    public void setCurrentUser(String username) {
        userLabel.setText("Welcome, " + username);
        log.info(username, "login", "User logged in to main panel");
    }

    public void setITSupport(boolean isITSupport) {
        if (this.isITSupport == isITSupport) {
            return; // Prevent unnecessary updates
        }

        this.isITSupport = isITSupport;

        if (isITSupport) {
            // Find the component that contains the navigation buttons
            Component[] components = getComponents();
            for (Component comp : components) {
                if (comp instanceof JPanel && comp != contentPanel) {
                    JPanel panel = (JPanel) comp;
                    if (!navigationButtons.containsKey("All Tickets")) {
                        addNavigationButton(panel, "All Tickets");
                    }
                    if (!navigationButtons.containsKey("Reports")) {
                        addNavigationButton(panel, "Reports");
                    }
                }
            }
        }

        revalidate();
        repaint();
    }
}