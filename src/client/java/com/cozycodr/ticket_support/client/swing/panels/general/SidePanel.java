package com.cozycodr.ticket_support.client.swing.panels.general;

import jakarta.annotation.PostConstruct;
import net.miginfocom.swing.MigLayout;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

@Component
public class SidePanel extends JPanel {

    private Consumer<String> onNavigationSelected;

    public SidePanel() {
        // Use MigLayout with fillx and insets; fixed preferred width.
        setLayout(new MigLayout("fillx, insets 10", "[grow]", "[]10[]10[]"));
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(250, 150));
        setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(220, 220, 220)));
        initPanel();
    }

    @PostConstruct
    public void postInit() {
        // Ensure initialization runs in case @PostConstruct is triggered later.
        initPanel();
    }

    private void initPanel() {
        removeAll();
        // Header label
        JLabel headerLabel = new JLabel("Hahns Ticket Tracking");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(headerLabel, "wrap, align center, gapbottom 20");

        // Navigation buttons
        JButton myTicketsBtn = createNavButton("My Tickets", true);
        myTicketsBtn.addActionListener(e -> {
            if (onNavigationSelected != null) {
                onNavigationSelected.accept("MyTickets");
            }
        });
        add(myTicketsBtn, "growx, wrap");

        JButton createTicketBtn = createNavButton("Create Ticket", false);
        createTicketBtn.addActionListener(e -> {
            if (onNavigationSelected != null) {
                onNavigationSelected.accept("CreateTicket");
            }
        });
        add(createTicketBtn, "growx, wrap");

        revalidate();
        repaint();
    }

    public void setOnNavigationSelected(Consumer<String> callback) {
        this.onNavigationSelected = callback;
    }

    private JButton createNavButton(String text, boolean active) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        button.setBackground(active ? new Color(230, 230, 230) : Color.WHITE);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(245, 245, 245));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(active ? new Color(230, 230, 230) : Color.WHITE);
            }
        });
        return button;
    }
}