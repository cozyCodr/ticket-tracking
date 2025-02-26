package com.cozycodr.ticket_support.client.swing.panels.ticket;

import com.cozycodr.ticket_support.client.dto.AuthDataResponse;
import com.cozycodr.ticket_support.client.dto.ticket.TicketResponse;
import com.cozycodr.ticket_support.client.enums.TicketStatus;
import com.cozycodr.ticket_support.client.service.ClientTicketService;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

public class TicketDetailsPanel extends JPanel {
    private final JLabel titleLabel;
    private final JLabel descriptionLabel;
    private final JLabel statusLabel;
    private final JLabel priorityLabel;
    private final JLabel categoryLabel;
    private final JLabel createdByLabel;
    private final JLabel createdAtLabel;
    private final JComboBox<TicketStatus> statusComboBox;
    private final JTextArea commentArea;
    private final JButton submitCommentButton;
    private final ClientTicketService ticketService;
    private AuthDataResponse currentUser;
    private TicketResponse currentTicket;
    private Consumer<Void> onBackPressed;

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public TicketDetailsPanel(ClientTicketService ticketService) {
        this.ticketService = ticketService;
        setLayout(new MigLayout("fillx, insets 20", "[grow]", "[]10[]10[]10[]10[]10[]10[]"));
        setBackground(Color.WHITE);

        // Initialize components
        titleLabel = createStyledLabel("", new Font("Arial", Font.BOLD, 24));
        statusLabel = createStyledLabel("", new Font("Arial", Font.BOLD, 14));
        priorityLabel = createStyledLabel("", new Font("Arial", Font.BOLD, 14));
        categoryLabel = createStyledLabel("", new Font("Arial", Font.BOLD, 14));
        descriptionLabel = createStyledLabel("", new Font("Arial", Font.PLAIN, 14));
        createdByLabel = createStyledLabel("", new Font("Arial", Font.ITALIC, 12));
        createdAtLabel = createStyledLabel("", new Font("Arial", Font.ITALIC, 12));

        // Status management for IT Support
        statusComboBox = new JComboBox<>(TicketStatus.values());
        statusComboBox.setVisible(false); // Hidden by default

        // Comment section
        commentArea = new JTextArea(4, 30);
        commentArea.setLineWrap(true);
        commentArea.setWrapStyleWord(true);

        submitCommentButton = new JButton("Add Comment");
        submitCommentButton.setEnabled(false);

        initializeUI();
    }

    private void initializeUI() {
        // Back button
        JButton backButton = new JButton("← Back to Tickets");
        styleBackButton(backButton);
        add(backButton, "wrap, gapbottom 20");

        // Add all components
        add(titleLabel, "wrap, growx");

        // Status panel with combo box for IT Support
        JPanel statusPanel = new JPanel(new MigLayout("insets 0", "[]10[]", "[]"));
        statusPanel.setOpaque(false);
        statusPanel.add(statusLabel);
        statusPanel.add(statusComboBox);
        add(statusPanel, "wrap");

        add(priorityLabel, "wrap");
        add(categoryLabel, "wrap");

        // Description section
        add(new JLabel("Description"), "wrap");
        add(descriptionLabel, "wrap, gapbottom 20");

        // Comments section
        add(new JLabel("Add Comment"), "wrap");
        add(new JScrollPane(commentArea), "wrap, growx");
        add(submitCommentButton, "wrap");

        // Meta information
        add(createdByLabel, "wrap");
        add(createdAtLabel, "wrap");

        // Configure back button
        backButton.addActionListener(e -> {
            if (onBackPressed != null) {
                onBackPressed.accept(null);
            }
        });

        // Configure status change handler
        statusComboBox.addActionListener(e -> {
            if (currentTicket != null && statusComboBox.isVisible()) {
                updateTicketStatus((TicketStatus) statusComboBox.getSelectedItem());
            }
        });

        // Configure comment submission
        submitCommentButton.addActionListener(e -> submitComment());
    }

    private void updateTicketStatus(TicketStatus newStatus) {
        if (currentTicket != null) {
            ticketService.updateTicketStatus(
                    currentTicket.getId(),
                    newStatus,
                    updatedTicket -> SwingUtilities.invokeLater(() -> {
                        currentTicket = updatedTicket;
                        updateStatusDisplay();
                    }),
                    error -> SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(this,
                                "Failed to update status: " + error,
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                        statusComboBox.setSelectedItem(currentTicket.getStatus());
                    })
            );
        }
    }

    private void submitComment() {
        String comment = commentArea.getText().trim();
        if (comment.isEmpty() || currentTicket == null) {
            return;
        }

        ticketService.addComment(
                currentTicket.getId(),
                comment,
                success -> SwingUtilities.invokeLater(() -> {
                    commentArea.setText("");
                    JOptionPane.showMessageDialog(this,
                            "Comment added successfully",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                }),
                error -> SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this,
                            "Failed to add comment: " + error,
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                })
        );
    }

    public void setCurrentUser(AuthDataResponse user) {
        this.currentUser = user;
        boolean isItSupport = user != null && user.isItSupport();
        statusComboBox.setVisible(isItSupport);
        submitCommentButton.setEnabled(isItSupport);
        commentArea.setEnabled(isItSupport);
    }

    public void displayTicket(TicketResponse ticket) {
        this.currentTicket = ticket;

        titleLabel.setText(String.format("#%s %s", ticket.getId().toString(), ticket.getTitle()));
        descriptionLabel.setText("<html>" + ticket.getDescription() + "</html>");
        updateStatusDisplay();
        priorityLabel.setText("Priority: " + ticket.getPriority());
        categoryLabel.setText("Category: " + ticket.getCategory());
        createdByLabel.setText("Created by: " + ticket.getCreatedBy());

        String dateText = "Created at: No date";
        if (ticket.getCreatedDate() != null) {
            dateText = "Created at: " + ticket.getCreatedDate().format(DATE_FORMATTER);
        }
        createdAtLabel.setText(dateText);

        if (currentUser != null && currentUser.isItSupport()) {
            statusComboBox.setSelectedItem(ticket.getStatus());
        }

        revalidate();
        repaint();
    }

    private void updateStatusDisplay() {
        if (currentTicket != null) {
            statusLabel.setText("Status: " + currentTicket.getStatus());
        }
    }

    private JLabel createStyledLabel(String text, Font font) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        return label;
    }

    private void styleBackButton(JButton button) {
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setForeground(new Color(0, 120, 212));
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setFocusPainted(false);
    }

    public void setOnBackPressed(Consumer<Void> callback) {
        this.onBackPressed = callback;
    }
}