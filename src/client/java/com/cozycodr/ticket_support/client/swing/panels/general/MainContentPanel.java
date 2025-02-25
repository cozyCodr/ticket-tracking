package com.cozycodr.ticket_support.client.swing.panels.general;

import com.cozycodr.ticket_support.client.swing.panels.ticket.CreateTicketPanel;
import com.cozycodr.ticket_support.client.swing.panels.ticket.MyTicketsPanel;
import jakarta.annotation.PostConstruct;
import net.miginfocom.swing.MigLayout;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;

@Component
public class MainContentPanel extends JPanel {

    private JLabel currentUserLabel;
    private JPanel headerPanel;
    private JPanel contentPanel;
    private JTabbedPane tabbedPane;
    private final CreateTicketPanel createTicketPanel;
    private final MyTicketsPanel myTicketsPanel;

    // Spring will inject the CreateTicketPanel and MyTicketsPanel beans.
    public MainContentPanel(CreateTicketPanel createTicketPanel, MyTicketsPanel myTicketsPanel) {
        this.createTicketPanel = createTicketPanel;
        this.myTicketsPanel = myTicketsPanel;
        setLayout(new MigLayout("fill, insets 0", "[grow]", "[][grow]"));
        setBackground(Color.WHITE);
    }

    @PostConstruct
    private void initComponents() {
        // Create header panel
        headerPanel = createHeaderPanel();
        // Initialize the tabbed pane and add tabs.
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("New Ticket", createTicketPanel);
        tabbedPane.addTab("My Tickets", myTicketsPanel);

        // Create content panel and add the tabbed pane.
        contentPanel = createContentPanel();
        contentPanel.add(tabbedPane, "grow");

        add(headerPanel, "dock north");
        add(contentPanel, "grow");
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new MigLayout("fillx, insets 10", "[grow][]", "[]"));
        panel.setBackground(new Color(51, 51, 51));

        // Initialize currentUserLabel to ensure it is not null.
        currentUserLabel = new JLabel("Welcome, User");
        currentUserLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        currentUserLabel.setForeground(Color.WHITE);
        panel.add(currentUserLabel, "wrap");

        // Create a search field panel similar to the React example.
        JPanel searchPanel = new JPanel(new MigLayout("insets 0", "[]push[]", "0"));
        searchPanel.setOpaque(false);
        JTextField searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(250, 30));
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));
        searchPanel.add(new JLabel("\uD83D\uDD0D"), "aligny center");
        searchPanel.add(searchField, "wrap, gapleft 5");
        panel.add(searchPanel, "growx");

        return panel;
    }

    private JPanel createContentPanel() {
        JPanel panel = new JPanel(new MigLayout("fill, insets 20", "[grow]", "[grow]"));
        panel.setBackground(Color.WHITE);
        return panel;
    }

    public void setCurrentUser(String username) {
        if (currentUserLabel != null) {
            currentUserLabel.setText("Welcome, " + username);
            myTicketsPanel.refreshTickets(username);
        }
    }

    public void setITSupport(boolean isITSupport) {
        // Optionally add UI modifications based on role.
    }

    public void switchToMyTicketsPanel() {
        if (tabbedPane != null && tabbedPane.getTabCount() > 1) {
            tabbedPane.setSelectedIndex(1);
        } else {
            System.err.println("Tab count insufficient for switching to 'My Tickets'");
        }
    }

    public void switchToCreateTicketPanel() {
        if (tabbedPane != null && tabbedPane.getTabCount() > 0) {
            tabbedPane.setSelectedIndex(0);
        } else {
            System.err.println("No tabs available for switching to 'New Ticket'");
        }
    }
}