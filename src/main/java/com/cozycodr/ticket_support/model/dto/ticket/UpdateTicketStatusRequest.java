package com.cozycodr.ticket_support.model.dto.ticket;

import com.cozycodr.ticket_support.model.enums.TicketStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateTicketStatusRequest {

    @NotNull(message = "Status is required")
    private TicketStatus status;
}
