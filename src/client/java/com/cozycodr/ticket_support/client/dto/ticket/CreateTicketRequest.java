package com.cozycodr.ticket_support.client.dto.ticket;

import com.cozycodr.ticket_support.client.enums.TicketCategory;
import com.cozycodr.ticket_support.client.enums.TicketPriority;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateTicketRequest {
    private String title;
    private String description;
    private TicketPriority priority;
    private TicketCategory category;
}
