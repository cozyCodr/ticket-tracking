package com.cozycodr.ticket_support.client.utils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;

public class DialogUtils {
    private static final int MAX_WIDTH = 400;  // Maximum width in pixels
    private static final int MAX_LINE_LENGTH = 50;  // Maximum characters per line
    private static final Font DIALOG_FONT = new Font("Arial", Font.PLAIN, 14);
    private static final Color PRIMARY_COLOR = new Color(51, 51, 51);

    public static void showErrorDialog(Component parentComponent, String message, String title) {
        // Create custom panel
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Format and wrap the message
        String formattedMessage = formatMessage(message);
        JLabel messageLabel = new JLabel("<html>" + formattedMessage + "</html>");
        messageLabel.setFont(DIALOG_FONT);
        messageLabel.setBorder(new EmptyBorder(0, 5, 0, 5));

        panel.add(messageLabel, BorderLayout.CENTER);

        // Configure and show the dialog
        JOptionPane optionPane = new JOptionPane(
                panel,
                JOptionPane.ERROR_MESSAGE,
                JOptionPane.DEFAULT_OPTION
        );

        // Create and configure the dialog
        JDialog dialog = optionPane.createDialog(parentComponent, title);
        dialog.setMinimumSize(new Dimension(300, 100));
        dialog.setMaximumSize(new Dimension(MAX_WIDTH, 400));

        // Center on parent
        if (parentComponent != null) {
            dialog.setLocationRelativeTo(parentComponent);
        }

        dialog.setVisible(true);
    }

    public static void showInfoDialog(Component parentComponent, String message, String title) {
        // Similar to error dialog but with different icon
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        String formattedMessage = formatMessage(message);
        JLabel messageLabel = new JLabel("<html>" + formattedMessage + "</html>");
        messageLabel.setFont(DIALOG_FONT);
        messageLabel.setBorder(new EmptyBorder(0, 5, 0, 5));

        panel.add(messageLabel, BorderLayout.CENTER);

        JOptionPane optionPane = new JOptionPane(
                panel,
                JOptionPane.INFORMATION_MESSAGE,
                JOptionPane.DEFAULT_OPTION
        );

        JDialog dialog = optionPane.createDialog(parentComponent, title);
        dialog.setMinimumSize(new Dimension(300, 100));
        dialog.setMaximumSize(new Dimension(MAX_WIDTH, 400));

        if (parentComponent != null) {
            dialog.setLocationRelativeTo(parentComponent);
        }

        dialog.setVisible(true);
    }

    public static boolean showConfirmDialog(Component parentComponent, String message, String title) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        String formattedMessage = formatMessage(message);
        JLabel messageLabel = new JLabel("<html>" + formattedMessage + "</html>");
        messageLabel.setFont(DIALOG_FONT);
        messageLabel.setBorder(new EmptyBorder(0, 5, 0, 5));

        panel.add(messageLabel, BorderLayout.CENTER);

        int result = JOptionPane.showConfirmDialog(
                parentComponent,
                panel,
                title,
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        return result == JOptionPane.YES_OPTION;
    }

    private static String formatMessage(String message) {
        if (message == null || message.isEmpty()) {
            return "";
        }

        // Split message into words
        String[] words = message.split("\\s+");
        java.util.List<String> lines = new ArrayList<>();
        StringBuilder currentLine = new StringBuilder();

        for (String word : words) {
            if (currentLine.length() + word.length() + 1 <= MAX_LINE_LENGTH) {
                if (!currentLine.isEmpty()) {
                    currentLine.append(" ");
                }
                currentLine.append(word);
            } else {
                lines.add(currentLine.toString());
                currentLine = new StringBuilder(word);
            }
        }

        if (!currentLine.isEmpty()) {
            lines.add(currentLine.toString());
        }

        // Join lines with HTML line breaks
        return String.join("<br>", lines);
    }
}
