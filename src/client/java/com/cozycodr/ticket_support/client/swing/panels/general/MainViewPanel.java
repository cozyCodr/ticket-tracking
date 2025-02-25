package com.cozycodr.ticket_support.client.swing.panels.general;

import com.cozycodr.ticket_support.client.swing.panels.ticket.CreateTicketPanel;
import com.cozycodr.ticket_support.client.swing.panels.ticket.MyTicketsPanel;
import jakarta.annotation.PostConstruct;
import net.miginfocom.swing.MigLayout;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;

@Component
public class MainViewPanel extends JPanel {

    private final SidePanel sidePanel;
    private final JPanel cardPanel; // main content area using CardLayout
    private final CardLayout cardLayout;
    private final CreateTicketPanel createTicketPanel;
    private final MyTicketsPanel myTicketsPanel;

    private static final String CREATE_TICKET_CARD = "CreateTicket";
    private static final String MY_TICKETS_CARD = "MyTickets";

    // Beans are injected by Spring.
    public MainViewPanel(SidePanel sidePanel, CreateTicketPanel createTicketPanel, MyTicketsPanel myTicketsPanel) {
        // Using MigLayout: fixed width for side panel, remaining area for main content.
        setLayout(new MigLayout("fill, insets 0", "[250!][grow]", "[grow]"));
        this.sidePanel = sidePanel;
        this.createTicketPanel = createTicketPanel;
        this.myTicketsPanel = myTicketsPanel;
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        initCards();
    }

    @PostConstruct
    public void init() {
        // Set up navigation callback.
        sidePanel.setOnNavigationSelected(navItem -> {
            if ("MyTickets".equals(navItem)) {
                cardLayout.show(cardPanel, MY_TICKETS_CARD);
                myTicketsPanel.refreshTickets(""); // refresh tickets as needed
            } else if ("CreateTicket".equals(navItem)) {
                cardLayout.show(cardPanel, CREATE_TICKET_CARD);
            }
            // Ensure the card panel is updated.
            cardPanel.revalidate();
            cardPanel.repaint();
        });
        add(sidePanel, "growy");
        add(cardPanel, "grow");
        // Set default view to "My Tickets" (or change to CREATE_TICKET_CARD if desired)
        cardLayout.show(cardPanel, MY_TICKETS_CARD);
    }

    private void initCards() {
        cardPanel.add(createTicketPanel, CREATE_TICKET_CARD);
        cardPanel.add(myTicketsPanel, MY_TICKETS_CARD);
    }

    public void setCurrentUser(String username) {
        // Passing the username to refresh the tickets.
        myTicketsPanel.refreshTickets(username);
    }

    public void setITSupport(boolean isITSupport) {
        // Optionally update UI as needed.
    }
}