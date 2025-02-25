package com.cozycodr.ticket_support.client.dto.ticket;

import com.cozycodr.ticket_support.client.enums.TicketCategory;
import com.cozycodr.ticket_support.client.enums.TicketPriority;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateTicketRequest {
    private String title;
    private String description;
    private TicketPriority priority;
    private TicketCategory category;
}
