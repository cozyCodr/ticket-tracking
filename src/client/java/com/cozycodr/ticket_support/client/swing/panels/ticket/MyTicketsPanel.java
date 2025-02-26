package com.cozycodr.ticket_support.client.swing.panels.ticket;

import com.cozycodr.ticket_support.client.dto.AuthDataResponse;
import com.cozycodr.ticket_support.client.dto.PageResponse;
import com.cozycodr.ticket_support.client.dto.ticket.TicketListResponse;
import com.cozycodr.ticket_support.client.dto.ticket.TicketResponse;
import com.cozycodr.ticket_support.client.service.ClientTicketService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import net.miginfocom.swing.MigLayout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
public class MyTicketsPanel extends JPanel {

    private final ClientTicketService ticketService;
    private final JPanel ticketsListPanel;
    private final CardLayout cardLayout;
    private final JPanel contentPanel;
    private final TicketDetailsPanel ticketDetailsPanel;
    private AuthDataResponse currentUser;

    private static final String TICKETS_LIST = "TICKETS_LIST";
    private static final String TICKET_DETAILS = "TICKET_DETAILS";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Autowired
    public MyTicketsPanel(ClientTicketService ticketService) {
        this.ticketService = ticketService;
        this.cardLayout = new CardLayout();
        this.contentPanel = new JPanel(cardLayout);

        // Use a more specific MigLayout configuration
        this.ticketsListPanel = new JPanel(new MigLayout(
                "fillx, insets 10, wrap 1", // Layout constraints
                "[grow]", // Column constraints
                "[]10[]" // Row constraints
        ));

        this.ticketDetailsPanel = new TicketDetailsPanel(ticketService);

        // Set preferred size
//        setPreferredSize(new Dimension(600, 400));

        initializeUI();
    }

    @PostConstruct
    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Setup header panel with title and filters for IT Support
        JPanel headerPanel = new JPanel(new MigLayout("fillx, insets 10", "[grow][]", "[]"));
        headerPanel.setBackground(Color.WHITE);

        JLabel headerLabel = new JLabel("Tickets");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        headerPanel.add(headerLabel, "cell 0 0");

        // Initialize the panels
        ticketsListPanel.setBackground(Color.WHITE);
        JScrollPane scrollPane = new JScrollPane(ticketsListPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        // Add components to content panel
        contentPanel.add(scrollPane, TICKETS_LIST);
        contentPanel.add(ticketDetailsPanel, TICKET_DETAILS);

        // Important: Show the tickets list by default
        cardLayout.show(contentPanel, TICKETS_LIST);

        // Add components to main panel
        add(headerPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);

        // Configure back button handler
        ticketDetailsPanel.setOnBackPressed(unused -> {
            cardLayout.show(contentPanel, TICKETS_LIST);
            refreshTickets(currentUser.getUsername());
        });

        // Force initial layout
        revalidate();
        repaint();
    }

    public void setCurrentUser(AuthDataResponse user) {
        this.currentUser = user;
        this.ticketDetailsPanel.setCurrentUser(user);
    }

    public void refreshTickets(String username) {
        if (currentUser == null) {
            log.warn("Attempted to refresh tickets without user context");
            return;
        }

        log.debug("Refreshing tickets for user: {} with role: {}",
                username, currentUser.getRole());

        ticketsListPanel.removeAll();

        // Add loading indicator
        JLabel loadingLabel = new JLabel("Loading tickets...");
        loadingLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        ticketsListPanel.add(loadingLabel, "wrap");
        ticketsListPanel.revalidate();
        ticketsListPanel.repaint();

        if (currentUser.isItSupport()) {
            log.info("Fetching all tickets for IT Support user");
            ticketService.fetchAllTickets(1, 50,
                    response -> {
                        log.debug("Received all tickets response");
                        handleTicketsResponse(response);
                    },
                    error -> {
                        log.error("Error fetching all tickets: {}", error);
                        handleError(error);
                    });
        } else {
            log.info("Fetching user tickets for: {}", username);
            ticketService.fetchMyTickets(1, 50,
                    response -> {
                        log.debug("Received user tickets response");
                        handleUserTicketsResponse(response);
                    },
                    error -> {
                        log.error("Error fetching user tickets: {}", error);
                        handleError(error);
                    });
        }
    }

    private void handleTicketsResponse(PageResponse<TicketResponse> response) {
        log.debug("Handling all tickets response: {}", response); // Add logging
        SwingUtilities.invokeLater(() -> {
            ticketsListPanel.removeAll();

            if (response == null || response.getContent() == null || response.getContent().isEmpty()) {
                log.debug("No tickets found in all tickets response");
                addNoTicketsLabel(currentUser.getUsername());
            } else {
                log.debug("Adding {} tickets to panel", response.getContent().size());
                for (TicketResponse ticket : response.getContent()) {
                    addTicketPanel(ticket);
                    log.debug("Added ticket: {}", ticket.getId());
                }
            }

            ticketsListPanel.revalidate();
            ticketsListPanel.repaint();
        });
    }

    private void handleUserTicketsResponse(TicketListResponse response) {
        log.debug("Handling user tickets response: {}", response); // Add logging
        SwingUtilities.invokeLater(() -> {
            ticketsListPanel.removeAll();

            if (response == null || response.getTickets() == null || response.getTickets().isEmpty()) {
                log.debug("No tickets found in user tickets response");
                addNoTicketsLabel(currentUser.getUsername());
            } else {
                log.debug("Adding {} tickets to panel", response.getTickets().size());
                for (TicketResponse ticket : response.getTickets()) {
                    addTicketPanel(ticket);
                    log.debug("Added ticket: {}", ticket.getId());
                }
            }

            ticketsListPanel.revalidate();
            ticketsListPanel.repaint();
        });
    }

    private void handleError(String error) {
        SwingUtilities.invokeLater(() -> {
            ticketsListPanel.removeAll();
            addErrorLabel(error);
            ticketsListPanel.revalidate();
            ticketsListPanel.repaint();
        });
    }

    private void showTicketDetails(TicketResponse ticket) {
        ticketDetailsPanel.displayTicket(ticket);
        cardLayout.show(contentPanel, TICKET_DETAILS);
    }

    private void addTicketPanel(TicketResponse ticket) {
        try {
            JPanel ticketPanel = createTicketPanel(ticket);
            ticketsListPanel.add(ticketPanel, "growx, wrap, gapbottom 10");
            log.debug("Successfully added ticket panel for ticket ID: {}", ticket.getId());
        } catch (Exception e) {
            log.error("Error adding ticket panel for ticket: {}", ticket.getId(), e);
        }
    }


    private JPanel createTicketPanel(TicketResponse ticket) {
        try {
            JPanel panel = new JPanel(new MigLayout("fillx, insets 15", "[grow][]", "[]5[]"));
            panel.setBackground(Color.WHITE);
            panel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(220, 220, 220)),
                    BorderFactory.createEmptyBorder(0, 0, 0, 0)
            ));

            // Add hover effect
            panel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    panel.setBackground(new Color(248, 249, 250));
                    panel.setCursor(new Cursor(Cursor.HAND_CURSOR));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    panel.setBackground(Color.WHITE);
                }

                @Override
                public void mouseClicked(MouseEvent e) {
                    showTicketDetails(ticket);
                }
            });

            // Ticket number and title
            String titleText = String.format("#%s %s",
                    ticket.getId() != null ? ticket.getId().toString() : "N/A",
                    ticket.getTitle() != null ? ticket.getTitle() : "Untitled");
            JLabel titleLabel = new JLabel(titleText);
            titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
            panel.add(titleLabel, "wrap");

            // Status and date on one line
            JPanel metaPanel = new JPanel(new MigLayout("insets 0", "[grow][]", "[]"));
            metaPanel.setOpaque(false);

            if (ticket.getStatus() != null) {
                JLabel statusLabel = createStatusLabel(ticket.getStatus().toString());
                metaPanel.add(statusLabel);
            }

            // Handle date safely
            if (ticket.getCreatedDate() != null) {
                JLabel dateLabel = new JLabel(ticket.getCreatedDate().format(DATE_FORMATTER));
                dateLabel.setForeground(Color.GRAY);
                dateLabel.setFont(new Font("Arial", Font.PLAIN, 12));
                metaPanel.add(dateLabel, "align right");
            }

            panel.add(metaPanel, "growx");

            return panel;
        } catch (Exception e) {
            log.error("Error creating ticket panel for ticket ID: {}",
                    ticket != null ? ticket.getId() : "null", e);
            // Return a simple error panel instead of failing
            JPanel errorPanel = new JPanel(new BorderLayout());
            errorPanel.add(new JLabel("Error displaying ticket"), BorderLayout.CENTER);
            return errorPanel;
        }
    }

    private JLabel createStatusLabel(String status) {
        JLabel label = new JLabel(status);
        label.setFont(new Font("Arial", Font.BOLD, 12));
        label.setOpaque(true);
        label.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));

        switch (status) {
            case "OPEN":
                label.setBackground(new Color(220, 245, 220));
                label.setForeground(new Color(40, 120, 40));
                break;
            case "IN_PROGRESS":
                label.setBackground(new Color(255, 245, 220));
                label.setForeground(new Color(180, 120, 40));
                break;
            case "CLOSED":
                label.setBackground(new Color(240, 240, 240));
                label.setForeground(new Color(80, 80, 80));
                break;
        }

        return label;
    }

    private void addNoTicketsLabel(String username) {
        JLabel label = new JLabel("No tickets found for: " + username);
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        label.setForeground(Color.GRAY);
        ticketsListPanel.add(label, "wrap, gaptop 20");
    }

    private void addErrorLabel(String errorMessage) {
        JLabel label = new JLabel("Error loading tickets: " + errorMessage);
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        label.setForeground(new Color(220, 53, 69));
        ticketsListPanel.add(label, "wrap");
    }
}