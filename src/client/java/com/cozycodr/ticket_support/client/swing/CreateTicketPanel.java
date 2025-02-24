package com.cozycodr.ticket_support.client.swing;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class CreateTicketPanel extends JPanel {
    private JTextField titleField;
    private JTextArea descriptionArea;
    private JComboBox<String> priorityCombo;
    private JComboBox<String> categoryCombo;

    public CreateTicketPanel() {
        setLayout(new MigLayout("fillx, insets 20", "[right][grow]", "[]10[]"));
        setBackground(Color.WHITE);
        initializeComponents();
        layoutComponents();
    }

    private void initializeComponents() {
        // Title
        titleField = new JTextField(20);

        // Description
        descriptionArea = new JTextArea(5, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);

        // Priority ComboBox
        priorityCombo = new JComboBox<>(new String[]{"Low", "Medium", "High"});

        // Category ComboBox
        categoryCombo = new JComboBox<>(new String[]{"Network", "Hardware", "Software", "Other"});
    }

    private void layoutComponents() {
        // Title section
        add(createSectionLabel("Create New Ticket"), "span 2, center, wrap");

        // Form fields
        add(createLabel("Title:"), "right");
        add(titleField, "growx, wrap");

        add(createLabel("Description:"), "right");
        add(new JScrollPane(descriptionArea), "growx, wrap");

        add(createLabel("Priority:"), "right");
        add(priorityCombo, "growx, wrap");

        add(createLabel("Category:"), "right");
        add(categoryCombo, "growx, wrap");

        // Submit button
        JButton submitButton = new JButton("Submit Ticket");
        submitButton.setBackground(new Color(0, 120, 212));
        submitButton.setForeground(Color.WHITE);
        submitButton.setFocusPainted(false);

        add(submitButton, "span 2, center, wrap");
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        return label;
    }

    private JLabel createSectionLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 18));
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        return label;
    }
}
