package com.cozycodr.ticket_support.client.swing.panels.ticket;

import com.cozycodr.ticket_support.client.dto.ticket.CreateTicketRequest;
import com.cozycodr.ticket_support.client.enums.TicketCategory;
import com.cozycodr.ticket_support.client.enums.TicketPriority;
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
    private JComboBox<TicketPriority> priorityComboBox;
    private JComboBox<TicketCategory> categoryComboBox;
    private JButton submitButton;

    @Autowired
    public CreateTicketPanel(ClientTicketService ticketService) {
        this.ticketService = ticketService;
        initializeUI();
    }

    private void initializeUI() {
        // Layout remains the same
        setLayout(new MigLayout("fill", "[right][grow]", "[][][][grow][][][][]"));
        setBackground(Color.WHITE);

        // Header
        JLabel header = new JLabel("New Ticket");
        header.setFont(new Font("Arial", Font.BOLD, 18));
        add(header, "span 2, align center, gapbottom 20, wrap");

        // Form fields
        add(createLabel("Title:"));
        titleField = new JTextField(30);
        titleField.setFont(new Font("Arial", Font.PLAIN, 14));
        add(titleField, "growx, wrap");

        add(createLabel("Description:"));
        descriptionArea = new JTextArea(5, 30);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setFont(new Font("Arial", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(descriptionArea);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane, "growx, growy, wrap");

        add(createLabel("Priority:"));
        priorityComboBox = new JComboBox<>(TicketPriority.values());
        styleComboBox(priorityComboBox);
        add(priorityComboBox, "growx, wrap");

        add(createLabel("Category:"));
        categoryComboBox = new JComboBox<>(TicketCategory.values());
        styleComboBox(categoryComboBox);
        add(categoryComboBox, "growx, wrap");

        submitButton = new JButton("Create Ticket");
        styleButton(submitButton);
        submitButton.addActionListener(e -> createNewTicket());
        add(submitButton, "span 2, align center, gaptop 20");
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        return label;
    }

    private void styleComboBox(JComboBox<?> comboBox) {
        comboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        comboBox.setBackground(Color.WHITE);
        comboBox.setPreferredSize(new Dimension(200, 30));
        ((JLabel)comboBox.getRenderer()).setPreferredSize(new Dimension(180, 25));
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
        TicketPriority priority = (TicketPriority) priorityComboBox.getSelectedItem();
        TicketCategory category = (TicketCategory) categoryComboBox.getSelectedItem();

        // Validate fields
        if (!validateFields(title, description)) {
            return;
        }

        // Create the DTO directly
        CreateTicketRequest request = CreateTicketRequest.builder()
                .title(title)
                .description(description)
                .priority(priority)
                .category(category)
                .build();

        submitButton.setEnabled(false);
        ticketService.createTicket(
                request,
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

    private boolean validateFields(String title, String description) {
        if (title.isEmpty() || description.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Title and Description cannot be empty",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (title.length() < 5 || title.length() > 255) {
            JOptionPane.showMessageDialog(this,
                    "Title must be between 5 and 255 characters",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (description.length() < 10) {
            JOptionPane.showMessageDialog(this,
                    "Description must be at least 10 characters",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    private void clearForm() {
        titleField.setText("");
        descriptionArea.setText("");
        priorityComboBox.setSelectedIndex(0);
        categoryComboBox.setSelectedIndex(0);
        titleField.requestFocus();
    }
}