package com.cozycodr.ticket_support.model.dto.ticket;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TicketDataResponse<T> {

    private T tickets;

    public static <T> TicketDataResponse<T> from(T ticketData){
        return TicketDataResponse.<T>builder()
                .tickets(ticketData)
                .build();
    }
}
