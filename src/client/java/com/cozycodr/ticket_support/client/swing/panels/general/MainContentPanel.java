package com.cozycodr.ticket_support.client.swing.panels.general;

import com.cozycodr.ticket_support.client.dto.ticket.CreateTicketRequest;
import com.cozycodr.ticket_support.client.swing.panels.ticket.CreateTicketPanel;
import com.cozycodr.ticket_support.client.swing.panels.ticket.MyTicketsPanel;
import jakarta.annotation.PostConstruct;
import net.miginfocom.swing.MigLayout;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    public MainContentPanel(CreateTicketPanel createTicketPanel, MyTicketsPanel myTicketsPanel) {
        this.createTicketPanel = createTicketPanel;
        this.myTicketsPanel = myTicketsPanel;
        setLayout(new MigLayout("fill, insets 0", "[grow]", "[][grow]"));
        setBackground(Color.WHITE);
        initComponents();
    }



    @PostConstruct
    private void initComponents() {
        headerPanel = createHeaderPanel();
        contentPanel = createContentPanel();
        tabbedPane.addTab("New Ticket", createTicketPanel);
        tabbedPane.addTab("My Tickets", myTicketsPanel);

        add(headerPanel, "dock north");
        add(contentPanel, "grow");
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new MigLayout("fillx, insets 10", "[grow][]", "[]"));
        panel.setBackground(new Color(51, 51, 51));

        // Initialize currentUserLabel here to ensure it is never null
        currentUserLabel = new JLabel("Welcome, User");
        currentUserLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        currentUserLabel.setForeground(Color.WHITE);
        panel.add(currentUserLabel, "wrap");

        // Create a search field panel similar to the react example
        JPanel searchPanel = new JPanel(new MigLayout("insets 0", "[]push[]", "0"));
        searchPanel.setOpaque(false);
        JTextField searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(250, 30));
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));
        // Using a Unicode magnifier as a placeholder icon
        searchPanel.add(new JLabel("\uD83D\uDD0D"), "aligny center");
        searchPanel.add(searchField, "wrap, gapleft 5");
        panel.add(searchPanel, "growx");

        return panel;
    }

    private JPanel createContentPanel() {
        JPanel panel = new JPanel(new MigLayout("fill, insets 20", "[grow]", "[grow]"));
        panel.setBackground(Color.WHITE);
        // Placeholder content for main area
        JLabel placeholder = new JLabel("Main Content Area", SwingConstants.CENTER);
        placeholder.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(placeholder, "grow");

        return panel;
    }

    public void setCurrentUser(String username) {
        // Ensure currentUserLabel is not null before setting text.
        if (currentUserLabel != null) {
            currentUserLabel.setText("Welcome, " + username);
            // Refresh the list of tickets for the current user
            myTicketsPanel.refreshTickets(username);
        }
    }

    public void setITSupport(boolean isITSupport) {
        // todo: Modify content or add additional UI components based on role if needed
    }
}