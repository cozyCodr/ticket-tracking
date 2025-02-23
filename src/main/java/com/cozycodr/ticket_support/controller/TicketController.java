package com.cozycodr.ticket_support.controller;

import com.cozycodr.ticket_support.helpers.ApiResponseBody;
import com.cozycodr.ticket_support.model.dto.*;
import com.cozycodr.ticket_support.model.dto.comments.AddCommentRequest;
import com.cozycodr.ticket_support.model.dto.comments.SingleCommentResponse;
import com.cozycodr.ticket_support.model.dto.ticket.*;
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
    public ResponseEntity<ApiResponseBody<TicketResponse>> createTicket(
            @Valid @RequestBody CreateTicketRequest request,
            @RequestHeader("X-User-Id") UUID userId
    ){
        return ticketService.createTicket(request, userId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "IT Support GET tickets",
            description = "Fetches paginated tickets for IT Support"
    )
    public ResponseEntity<ApiResponseBody<TicketDataResponse<PageResponse<TicketResponse>>>> getTickets(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ){
        return ticketService.getTickets(page, size);
    }

    @GetMapping("/{ticketId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Get Ticket By Id",
            description = "Fetches ticket by its ID"
    )
    public ResponseEntity<ApiResponseBody<SingleTicketResponse>> getTicketById(
            @PathVariable(name = "ticketId") UUID ticketId
    ){
        return ticketService.getTicketById(ticketId);
    }

    @PostMapping("/{ticketId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Add Comment",
            description = "Adds new comment to ticket"
    )
    public ResponseEntity<ApiResponseBody<SingleCommentResponse>> addCommentToTicket(
            @PathVariable("ticketId") UUID ticketId,
            @RequestBody AddCommentRequest body,
            @RequestHeader("Authorization") String authHeader
    ){
        return ticketService.addCommentTicket(ticketId, body, authHeader);
    }

    @PatchMapping("/{ticketId}/status")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Updates ticket status",
            description = "changes the status of a ticket"
    )
    public ResponseEntity<ApiResponseBody<SingleTicketResponse>> updateTicketStatus(
            @PathVariable("ticketId") UUID ticketId,
            @RequestBody UpdateTicketStatusRequest body,
            @RequestHeader("Authorization") String authHeader
    ){
        return ticketService.updateTicketStatus(ticketId, body, authHeader);
    }


    @GetMapping("/user")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Get User Tickets",
            description = "Fetches tickets opened by specific user"
    )
    public ResponseEntity<ApiResponseBody<TicketDataResponse<TicketListResponse>>> getTicketsByCreator(
            @RequestHeader(name = "X-User-Id") UUID userId,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ){
        return ticketService.getTicketsByCreator(userId, page, size);
    }
}
