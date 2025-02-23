package com.cozycodr.ticket_support.model.dto.ticket;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SingleTicketResponse {
    private TicketResponse ticket;
}
