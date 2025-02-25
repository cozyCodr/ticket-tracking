package com.cozycodr.ticket_support.client.swing.panels.ticket;

import com.cozycodr.ticket_support.client.service.ClientTicketService;
import jakarta.annotation.PostConstruct;
import net.miginfocom.swing.MigLayout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
        initComponents();
    }

    @PostConstruct
    private void initComponents() {
        setLayout(new MigLayout("fill", "[grow]", "[][][grow][]"));
        setBackground(Color.WHITE);

        JLabel header = new JLabel("New Ticket");
        header.setFont(new Font("Arial", Font.BOLD, 18));
        add(header, "wrap");

        add(new JLabel("Title:"), "wrap");
        titleField = new JTextField(30);
        add(titleField, "wrap, growx");

        add(new JLabel("Description:"), "wrap");
        descriptionArea = new JTextArea(5, 30);
        descriptionArea.setLineWrap(true);
        add(new JScrollPane(descriptionArea), "wrap, growx");

        submitButton = new JButton("Create Ticket");
        submitButton.addActionListener(e -> createNewTicket());
        add(submitButton, "align center");
    }

    private void createNewTicket() {
        String title = titleField.getText().trim();
        String description = descriptionArea.getText().trim();

        if (title.isEmpty() || description.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Title and Description cannot be empty", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String createTicketJson = String.format("{\"title\":\"%s\", \"description\":\"%s\"}", title, description);

        ticketService.createTicket(
                createTicketJson,
                ticketResponse -> SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this, "Ticket created with ID: " + ticketResponse.getId(), "Ticket Created", JOptionPane.INFORMATION_MESSAGE);
                    clearForm();
                }),
                error -> SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this, "Error creating ticket: " + error, "Error", JOptionPane.ERROR_MESSAGE);
                })
        );
    }

    private void clearForm() {
        titleField.setText("");
        descriptionArea.setText("");
    }
}