package com.cozycodr.ticket_support;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;

@SpringBootApplication
public class TicketServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(TicketServerApplication.class, args);
	}

}
