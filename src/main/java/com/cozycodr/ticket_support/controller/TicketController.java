package com.cozycodr.ticket_support.controller;

import com.cozycodr.ticket_support.helpers.ApiResponseBody;
import com.cozycodr.ticket_support.model.dto.CreateTicketRequest;
import com.cozycodr.ticket_support.service.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/tickets")
@RequiredArgsConstructor
@Tag(name = "Tickets", description = "Ticket management APIs")
public class TicketController {

    private final TicketService ticketService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Create a new ticket",
            description = "Creates a new support ticket with the provided details"
    )
    public ResponseEntity<ApiResponseBody> createTicket(
            @Valid @RequestBody CreateTicketRequest request,
            @RequestHeader("X-User-Id") UUID userId
    ){
        return ticketService.createTicket(request, userId);
    }

}
