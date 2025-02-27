package com.cozycodr.ticket_support.model.dto.ticket;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class TicketListResponse {

    List<TicketResponse> tickets;

}
