package com.cozycodr.ticket_support.service;

import com.cozycodr.ticket_support.exception.ResourceNotFoundException;
import com.cozycodr.ticket_support.helpers.ApiResponseBody;
import com.cozycodr.ticket_support.model.dto.*;
import com.cozycodr.ticket_support.model.entity.Comment;
import com.cozycodr.ticket_support.model.entity.Ticket;
import com.cozycodr.ticket_support.model.entity.User;
import com.cozycodr.ticket_support.repository.CommentRepository;
import com.cozycodr.ticket_support.repository.TicketRepository;
import com.cozycodr.ticket_support.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.cozycodr.ticket_support.helpers.ResponseHelpers.buildSuccessResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final AuditLogService auditLogService;

    /**
     * Creates a New Ticket
     * @param request contains details of ticket
     * @param userId id of user creating the ticket
     * @return TicketResponse, details of newly created ticket
     */
    public ResponseEntity<ApiResponseBody> createTicket(CreateTicketRequest request, UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found!"));

        Ticket newTicket = Ticket.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .priority(request.getPriority())
                .category(request.getCategory())
                .raisedBy(user)
                .build();
        ticketRepository.save(newTicket);

        // Link to User
        user.addTicket(newTicket);
        userRepository.save(user);

        // Create Log
        auditLogService.logNewTicketCreated(user, newTicket);

        return buildSuccessResponse(
                HttpStatus.CREATED,
                "Ticket created successfully",
                Map.of("ticket", buildTicketResponse(newTicket))
        );

    }

    /**
     *  Fetches a ticket by its ID
     * @param ticketId id of the ticket to be fetched
     * @return TicketResponse, details of the ticket to be returned
     */
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponseBody> getTicketById(UUID ticketId){
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket Not Found"));

        Pageable pageable = PageRequest.of(0, 10);

        // Get Last 10 comments on ticket
        List<Comment> comments = commentRepository
                .findAllByTicket_IdOrderByCreatedDateDesc(ticketId, pageable);

        TicketResponse response = buildTicketResponse(ticket, comments);

        return buildSuccessResponse(HttpStatus.OK, "Fetched ticket", Map.of("ticket", response));

    }

    /**
     * Fetches tickets created by a user
     * @param userId user id whose tickets to fetch
     * @param page page number of tickets, default is 1
     * @param size size of tickets per page, default is 10
     * @return a list of the users tickets
     */
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponseBody> getTicketsByCreator(UUID userId, int page, int size){

        Pageable pageable = PageRequest.of((page - 1), size);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Page<Ticket> tickets = ticketRepository.findTicketsByRaisedBy(user, pageable);
        List<TicketResponse> response = tickets.getContent().stream().map(this::buildTicketResponse).toList();

        Map<String, Object> data = Map.of("tickets", response);
        return buildSuccessResponse(HttpStatus.OK, "Fetched tickets", data);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponseBody> getTickets(int page, int size){
        Pageable pageable = PageRequest.of((page - 1), size);
        Page<Ticket> tickets = ticketRepository.findAllTicketsOrderByCreatedDateDesc(pageable);
        PageResponse<Object> response = buildTicketPageResponse(tickets);

        Map<String, Object> data = Map.of("tickets", response);
        return buildSuccessResponse(HttpStatus.OK, "Fetched tickets", data);
    }

    // ============= UTIL METHODS =================
    public PageResponse<Object> buildTicketPageResponse(Page<Ticket> ticketsPage) {
        List<TicketResponse> content = ticketsPage.getContent()
                .stream()
                .map(this::buildTicketResponse)
                .toList();

        return PageResponse.builder()
                .content(Collections.singletonList(content))
                .pageSize(ticketsPage.getSize())
                .totalPages(ticketsPage.getTotalPages())
                .totalElements(ticketsPage.getTotalElements())
                .first(ticketsPage.isFirst())
                .last(ticketsPage.isLast())
                .hasNext(ticketsPage.hasNext())
                .build();
    }

    public TicketResponse buildTicketResponse(Ticket ticket){
        return TicketResponse.builder()
                .id(ticket.getId())
                .title(ticket.getTitle())
                .description(ticket.getDescription())
                .priority(ticket.getPriority())
                .status(ticket.getStatus())
                .category(ticket.getCategory())
                .build();
    }

    public TicketResponse buildTicketResponse(Ticket ticket, List<Comment>comments){
        List <CommentResponse> last10comments = comments.stream()
                .map(this::buildCommentResponse)
                .toList();

        return TicketResponse.builder()
                .id(ticket.getId())
                .title(ticket.getTitle())
                .description(ticket.getDescription())
                .priority(ticket.getPriority())
                .status(ticket.getStatus())
                .category(ticket.getCategory())
                .last10Comments(last10comments)
                .build();
    }

    public CommentResponse buildCommentResponse(Comment c){
        Commenter commenter = Commenter.builder()
                .id(c.getCommenter().getId())
                .username(c.getCommenter().getUsername())
                .build();

        return CommentResponse.builder()
                .id(c.getId())
                .message(c.getMessage())
                .commenter(commenter)
                .createdDate(c.getCreatedDate())
                .updatedDate(c.getUpdatedDate())
                .build();
    }
}
