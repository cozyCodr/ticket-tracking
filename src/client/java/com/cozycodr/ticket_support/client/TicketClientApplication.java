package com.cozycodr.ticket_support.client;

import com.cozycodr.ticket_support.client.swing.frames.ApplicationFrame;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;

import javax.swing.*;

@Slf4j
@SpringBootApplication(exclude = {
        DataSourceAutoConfiguration.class,
        SecurityAutoConfiguration.class
})
public class TicketClientApplication {

    public static void main(String[] args) {
        // Set system look and feel before starting Spring
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            log.error("Failed to set system look and feel", e);
        }

        // Set client profile
        System.setProperty("spring.profiles.active", "client");

        // Create and configure Spring application
        SpringApplication app = new SpringApplication(TicketClientApplication.class);
        app.setWebApplicationType(WebApplicationType.NONE);

        // Run Spring and get the application context
        ConfigurableApplicationContext context = app.run(args);

        // Show the main frame
        SwingUtilities.invokeLater(() -> {
            ApplicationFrame frame = context.getBean(ApplicationFrame.class);
            frame.setVisible(true);
        });
    }
}
