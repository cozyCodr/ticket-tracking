package com.cozycodr.ticket_support.service;

import com.cozycodr.ticket_support.exception.ResourceNotFoundException;
import com.cozycodr.ticket_support.helpers.ApiResponseBody;
import com.cozycodr.ticket_support.model.dto.*;
import com.cozycodr.ticket_support.model.dto.comments.AddCommentRequest;
import com.cozycodr.ticket_support.model.dto.comments.CommentResponse;
import com.cozycodr.ticket_support.model.dto.comments.Commenter;
import com.cozycodr.ticket_support.model.dto.comments.SingleCommentResponse;
import com.cozycodr.ticket_support.model.dto.ticket.*;
import com.cozycodr.ticket_support.model.entity.Comment;
import com.cozycodr.ticket_support.model.entity.Ticket;
import com.cozycodr.ticket_support.model.entity.User;
import com.cozycodr.ticket_support.model.enums.TicketStatus;
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

import java.util.List;
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
    private final JwtService jwtService;

    /**
     * Creates a New Ticket
     * @param request contains details of ticket
     * @param userId id of user creating the ticket
     * @return TicketResponse, details of newly created ticket
     */
    @Transactional
    public ResponseEntity<ApiResponseBody<TicketResponse>> createTicket(CreateTicketRequest request, UUID userId) {
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

        var data = buildTicketResponse(newTicket);

        return buildSuccessResponse(
                HttpStatus.CREATED,
                "Ticket created successfully",
                data
        );

    }

    /**
     *  Fetches a ticket by its ID
     * @param ticketId id of the ticket to be fetched
     * @return TicketResponse, details of the ticket to be returned
     */
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponseBody<SingleTicketResponse>> getTicketById(UUID ticketId){
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket Not Found"));

        Pageable pageable = PageRequest.of(0, 10);

        // Get Last 10 comments on ticket
        List<Comment> comments = commentRepository
                .findAllByTicket_IdOrderByCreatedDateDesc(ticketId, pageable);

        var data = buildSingleTicketResponse(buildTicketResponse(ticket, comments));

        return buildSuccessResponse(HttpStatus.OK, "Fetched ticket", data);

    }

    /**
     * Fetches tickets created by a user
     * @param userId user id whose tickets to fetch
     * @param page page number of tickets, default is 1
     * @param size size of tickets per page, default is 10
     * @return a list of the users tickets
     */
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponseBody<TicketDataResponse<TicketListResponse>>> getTicketsByCreator(UUID userId, int page, int size){

        Pageable pageable = PageRequest.of((page - 1), size);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Page<Ticket> tickets = ticketRepository.findTicketsByRaisedBy(user, pageable);
        TicketListResponse response = buildTicketListResponse(tickets.getContent());

        var data = TicketDataResponse.from(response);
        return buildSuccessResponse(HttpStatus.OK, "Fetched tickets", data);
    }

    /**
     * Fetches Paginated List of tickets
     * @param page page number
     * @param size of tickets per page to be returned
     * @return a page of the tickets
     */
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponseBody<TicketDataResponse<PageResponse<TicketResponse>>>> getTickets(int page, int size){
        Pageable pageable = PageRequest.of((page - 1), size);
        Page<Ticket> tickets = ticketRepository.findAllTicketsOrderByCreatedDateDesc(pageable);
        PageResponse<TicketResponse> response = buildTicketPageResponse(tickets);

        var data = TicketDataResponse.from(response);
        return buildSuccessResponse(HttpStatus.OK, "Fetched tickets", data);
    }

    /**
     * Add a comment to an existing ticket
     * @param ticketId Id of the ticket to which the comment will be added
     * @param body request body with the message
     * @param authHeader authorization header
     * @return the created comment
     */
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponseBody<SingleCommentResponse>> addCommentTicket(UUID ticketId, AddCommentRequest body, String authHeader) {

        // Get User ID from auth Header and Fetch User
        UUID userId = UUID.fromString(jwtService.extractUserIdFromAuthHeader(authHeader));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid User ID"));

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket Not Found"));

        // Create Comment
        Comment comment = Comment.builder()
                .message(body.getMessage())
                .ticket(ticket)
                .commenter(user)
                .build();
        commentRepository.save(comment);

        // Add comment to user
        user.addComment(comment);
        userRepository.save(user);

        // Add Comment to ticket
        ticket.addComment(comment);
        ticketRepository.save(ticket);

        // log
        auditLogService.logCommentAddedToTicket(comment, ticket, user);

        // Build data
        var data = buildSingleCommentResponse(buildCommentResponse(comment));

        return buildSuccessResponse(HttpStatus.CREATED, "New Comment added", data);
    }

    /**
     * Updates the value of TicketStatus on a ticket
     * @param ticketId ID of the ticket whose status is being updated
     * @param body request body containing the new status
     * @param authHeader authorization header
     * @return an updated ticket
     */
    @Transactional
    public ResponseEntity<ApiResponseBody<SingleTicketResponse>> updateTicketStatus(UUID ticketId, UpdateTicketStatusRequest body, String authHeader) {
        TicketStatus beforeStatus;

        // Get User ID from auth Header and Fetch User
        UUID userId = UUID.fromString(jwtService.extractUserIdFromAuthHeader(authHeader));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid User ID"));

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket Not Found"));

        // Set Before Status
        beforeStatus = ticket.getStatus();

        // Update Status
        ticket.setStatus(body.getStatus());
        ticketRepository.save(ticket);

        // log
        auditLogService.logTicketStatusChange(beforeStatus, body.getStatus(), ticket, user);

        // Build Data Object
        var data = buildSingleTicketResponse(buildTicketResponse(ticket));

        return buildSuccessResponse(HttpStatus.OK, "Ticket status updated", data);
    }


    // ============= UTIL METHODS =================
    public PageResponse<TicketResponse> buildTicketPageResponse(Page<Ticket> ticketsPage) {
        List<TicketResponse> content = ticketsPage.getContent()
                .stream()
                .map(this::buildTicketResponse)
                .toList();

        return PageResponse.<TicketResponse>builder()
                .content(content)
                .pageSize(ticketsPage.getSize())
                .totalPages(ticketsPage.getTotalPages())
                .totalElements(ticketsPage.getTotalElements())
                .first(ticketsPage.isFirst())
                .last(ticketsPage.isLast())
                .hasNext(ticketsPage.hasNext())
                .build();
    }

    public TicketListResponse buildTicketListResponse(List<Ticket> tickets){
        var ticketList = tickets.stream().map(this::buildTicketResponse).toList();
        return TicketListResponse.builder()
                .tickets(ticketList)
                .build();
    }

    public SingleTicketResponse buildSingleTicketResponse(TicketResponse ticketResponse){
        return SingleTicketResponse.builder()
                .ticket(ticketResponse)
                .build();
    }

    public SingleCommentResponse buildSingleCommentResponse(CommentResponse commentResponse){
        return SingleCommentResponse.builder()
                .comment(commentResponse)
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

    /**
     * Builds a basic comment object
     * @param c a comment
     * @return a basic comment object
     */
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
