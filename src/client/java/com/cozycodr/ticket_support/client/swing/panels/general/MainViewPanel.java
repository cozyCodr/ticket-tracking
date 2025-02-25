package com.cozycodr.ticket_support.client.swing.panels.general;

import com.cozycodr.ticket_support.client.swing.panels.ticket.CreateTicketPanel;
import com.cozycodr.ticket_support.client.swing.panels.ticket.MyTicketsPanel;
import jakarta.annotation.PostConstruct;
import net.miginfocom.swing.MigLayout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;

@Component
public class MainViewPanel extends JPanel {

    private SidePanel sidePanel;
    private MainContentPanel mainContentPanel;
    private final CreateTicketPanel createTicketPanel;
    private final MyTicketsPanel myTicketsPanel;

    @Autowired
    public MainViewPanel(MyTicketsPanel myTicketsPanel, CreateTicketPanel createTicketPanel) {
        // Left fixed (250px) for side panel and growing main panel
        setLayout(new MigLayout("fill, insets 0", "[250!][grow]", "[grow]"));
        this.createTicketPanel = createTicketPanel;
        this.myTicketsPanel = myTicketsPanel;
    }

    @PostConstruct
    private void init() {
        sidePanel = new SidePanel();
        mainContentPanel = new MainContentPanel(createTicketPanel, myTicketsPanel);

        add(sidePanel, "growy");
        add(mainContentPanel, "grow");
    }

    public void setCurrentUser(String username) {
        mainContentPanel.setCurrentUser(username);
    }

    public void setITSupport(boolean isITSupport) {
        mainContentPanel.setITSupport(isITSupport);
    }
}
