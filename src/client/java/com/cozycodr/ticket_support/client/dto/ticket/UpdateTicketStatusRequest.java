package com.cozycodr.ticket_support.client.dto.ticket;


import com.cozycodr.ticket_support.client.enums.TicketStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateTicketStatusRequest {
    private TicketStatus status;
}
