package com.cozycodr.ticket_support.client.swing.panels.ticket;

import com.cozycodr.ticket_support.client.dto.ticket.TicketResponse;
import com.cozycodr.ticket_support.client.service.ClientTicketService;
import jakarta.annotation.PostConstruct;
import net.miginfocom.swing.MigLayout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.util.List;

@Component
public class MyTicketsPanel extends JPanel {

    private final ClientTicketService ticketService;
    private final JPanel ticketsListPanel;

    @Autowired
    public MyTicketsPanel(ClientTicketService ticketService) {
        this.ticketService = ticketService;
        this.ticketsListPanel = new JPanel(new MigLayout("fillx, insets 10", "[grow]", "[]10[]"));
        initializeUI();
    }

    @PostConstruct
    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        JScrollPane scrollPane = new JScrollPane(ticketsListPanel);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void refreshTickets(String username) {
        ticketService.fetchMyTickets(1, 10,
                ticketList -> SwingUtilities.invokeLater(() -> {
                    ticketsListPanel.removeAll();
                    List<TicketResponse> tickets = ticketList.getTickets();
                    if (tickets == null || tickets.isEmpty()) {
                        ticketsListPanel.add(new JLabel("No tickets found for: " + username), "wrap");
                    } else {
                        for (TicketResponse ticket : tickets) {
                            JPanel ticketPanel = new JPanel(new MigLayout("fillx, insets 5", "[grow]", "[]"));
                            ticketPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
                            ticketPanel.setBackground(Color.WHITE);
                            JLabel titleLabel = new JLabel("Ticket #" + ticket.getId() + ": " + ticket.getTitle());
                            JLabel descLabel = new JLabel("<html>" + ticket.getDescription() + "</html>");
                            ticketPanel.add(titleLabel, "wrap");
                            ticketPanel.add(descLabel, "growx");
                            ticketsListPanel.add(ticketPanel, "growx, wrap");
                        }
                    }
                    ticketsListPanel.revalidate();
                    ticketsListPanel.repaint();
                }),
                errorMessage -> SwingUtilities.invokeLater(() -> {
                    ticketsListPanel.removeAll();
                    ticketsListPanel.add(new JLabel("Error loading tickets: " + errorMessage), "wrap");
                    ticketsListPanel.revalidate();
                    ticketsListPanel.repaint();
                })
        );
    }
}