package com.cozycodr.ticket_support.client.dto.ticket;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketDataResponse<T> {

    private T tickets;

    public static <T> TicketDataResponse<T> from(T ticketData){
        return TicketDataResponse.<T>builder()
                .tickets(ticketData)
                .build();
    }
}
