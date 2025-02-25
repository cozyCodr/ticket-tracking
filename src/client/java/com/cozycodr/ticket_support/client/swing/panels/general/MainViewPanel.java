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
    private final JPanel cardPanel;
    private final CardLayout cardLayout;
    private final CreateTicketPanel createTicketPanel;
    private final MyTicketsPanel myTicketsPanel;
    private String currentUsername;

    private static final String CREATE_TICKET_CARD = "CreateTicket";
    private static final String MY_TICKETS_CARD = "MyTickets";

    public MainViewPanel(SidePanel sidePanel, CreateTicketPanel createTicketPanel, MyTicketsPanel myTicketsPanel) {
        setLayout(new MigLayout("fill, insets 0", "[250!][grow]", "[grow]"));
        this.sidePanel = sidePanel;
        this.createTicketPanel = createTicketPanel;
        this.myTicketsPanel = myTicketsPanel;
        this.cardLayout = new CardLayout();
        this.cardPanel = new JPanel(cardLayout);
        cardPanel.setBackground(Color.WHITE);
        initCards();
    }

    @PostConstruct
    public void init() {
        sidePanel.setOnNavigationSelected(navItem -> {
            if (MY_TICKETS_CARD.equals(navItem)) {
                cardLayout.show(cardPanel, MY_TICKETS_CARD);
                if (currentUsername != null) {
                    myTicketsPanel.refreshTickets(currentUsername);
                }
            } else if (CREATE_TICKET_CARD.equals(navItem)) {
                cardLayout.show(cardPanel, CREATE_TICKET_CARD);
            }
            sidePanel.setActiveButton(navItem);
            cardPanel.revalidate();
            cardPanel.repaint();
        });

        add(sidePanel, "growy");
        add(cardPanel, "grow");

        // Set default view to My Tickets
        cardLayout.show(cardPanel, MY_TICKETS_CARD);
        sidePanel.setActiveButton(MY_TICKETS_CARD);
    }

    private void initCards() {
        cardPanel.add(createTicketPanel, CREATE_TICKET_CARD);
        cardPanel.add(myTicketsPanel, MY_TICKETS_CARD);
    }

    public void setCurrentUser(String username) {
        this.currentUsername = username;
        myTicketsPanel.refreshTickets(username);
    }

    public void setITSupport(boolean isITSupport) {
        // Implement IT support specific functionality if needed
    }
}