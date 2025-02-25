package com.cozycodr.ticket_support.client.service;

import com.cozycodr.ticket_support.client.dto.ticket.CreateTicketRequest;
import com.cozycodr.ticket_support.client.dto.ticket.TicketResponse;

import java.util.List;

public interface TicketServiceInterface {
    TicketResponse createTicket(CreateTicketRequest ticket);
    List<TicketResponse> getMyTickets(String username);
}
