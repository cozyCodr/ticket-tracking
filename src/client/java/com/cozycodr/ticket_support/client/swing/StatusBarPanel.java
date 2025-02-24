package com.cozycodr.ticket_support.client.swing;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import net.miginfocom.swing.MigLayout;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Component
@Slf4j
public class StatusBarPanel extends JPanel {
    private final JLabel clockLabel;
    private final JLabel userLabel;
    private final JLabel connectionLabel;
    private Timer clockTimer;

    public StatusBarPanel() {
        setLayout(new MigLayout("fillx, insets 5", "[][][grow][]"));
        setBackground(new Color(245, 245, 245));
        setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(200, 200, 200)));

        Font monoFont = new Font(Font.MONOSPACED, Font.PLAIN, 12);

        userLabel = new JLabel("Not logged in");
        userLabel.setFont(monoFont);
        userLabel.setForeground(Color.DARK_GRAY);

        connectionLabel = new JLabel("Disconnected");
        connectionLabel.setFont(monoFont);
        connectionLabel.setForeground(Color.RED);

        clockLabel = new JLabel();
        clockLabel.setFont(monoFont);
        clockLabel.setForeground(Color.DARK_GRAY);

        add(new JLabel("User:"), "");
        add(userLabel, "");
        add(connectionLabel, "grow");
        add(clockLabel, "right");

        startClock();
    }

    private void startClock() {
        clockTimer = new Timer(1000, e -> updateClock());
        clockTimer.start();
        updateClock();
    }

    private void updateClock() {
        LocalDateTime utcNow = LocalDateTime.now(ZoneOffset.UTC);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        clockLabel.setText("UTC: " + utcNow.format(formatter));
    }

    public void setCurrentUser(String username) {
        userLabel.setText(username);
        log.info(username, "status update", "Updated status bar user");
    }

    public void setCurrentTime(String time) {
        clockLabel.setText("UTC: " + time);
    }

    public void setConnectionStatus(boolean connected) {
        connectionLabel.setText(connected ? "Connected" : "Disconnected");
        connectionLabel.setForeground(connected ? new Color(0, 120, 0) : Color.RED);
    }

    @PreDestroy
    public void cleanup() {
        if (clockTimer != null) {
            clockTimer.stop();
        }
    }
}
