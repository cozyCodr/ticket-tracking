package com.cozycodr.ticket_support.client.swing.panels.ticket;

import com.cozycodr.ticket_support.client.service.ClientTicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

@Component
public class CreateTicketPanel extends JPanel {

    private final ClientTicketService ticketService;
    private JTextField titleField;
    private JTextArea descriptionArea;
    private JButton submitButton;

    @Autowired
    public CreateTicketPanel(ClientTicketService ticketService) {
        this.ticketService = ticketService;
        // Initialize components in constructor only
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new MigLayout("fill", "[grow]", "[][][][grow][]"));
        setBackground(Color.WHITE);

        // Header
        JLabel header = new JLabel("New Ticket");
        header.setFont(new Font("Arial", Font.BOLD, 18));
        add(header, "wrap, gapbottom 20");

        // Title field
        add(createLabel("Title:"), "wrap");
        titleField = new JTextField(30);
        titleField.setFont(new Font("Arial", Font.PLAIN, 14));
        add(titleField, "wrap, growx");

        // Description area
        add(createLabel("Description:"), "wrap");
        descriptionArea = new JTextArea(5, 30);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setFont(new Font("Arial", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(descriptionArea);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane, "wrap, growx, growy");

        // Submit button
        submitButton = new JButton("Create Ticket");
        styleButton(submitButton);
        submitButton.addActionListener(e -> createNewTicket());
        add(submitButton, "align center, gaptop 20");
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        return label;
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setPreferredSize(new Dimension(120, 35));
        button.setBackground(new Color(0, 120, 212));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0, 100, 180));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0, 120, 212));
            }
        });
    }

    private void createNewTicket() {
        String title = titleField.getText().trim();
        String description = descriptionArea.getText().trim();

        if (title.isEmpty() || description.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Title and Description cannot be empty",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Escape special characters in JSON
        title = title.replace("\"", "\\\"");
        description = description.replace("\"", "\\\"");
        String createTicketJson = String.format("{\"title\":\"%s\", \"description\":\"%s\"}",
                title, description);

        submitButton.setEnabled(false);
        ticketService.createTicket(
                createTicketJson,
                ticketResponse -> SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this,
                            "Ticket created with ID: " + ticketResponse.getId(),
                            "Ticket Created",
                            JOptionPane.INFORMATION_MESSAGE);
                    clearForm();
                    submitButton.setEnabled(true);
                }),
                error -> SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this,
                            "Error creating ticket: " + error,
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    submitButton.setEnabled(true);
                })
        );
    }

    private void clearForm() {
        titleField.setText("");
        descriptionArea.setText("");
        titleField.requestFocus();
    }
}