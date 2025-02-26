package com.cozycodr.ticket_support.client.service;

import com.cozycodr.ticket_support.client.config.ObjectMapperConfig;
import com.cozycodr.ticket_support.client.dto.ApiResponseBody;
import com.cozycodr.ticket_support.client.dto.PageResponse;
import com.cozycodr.ticket_support.client.dto.comment.AddCommentRequest;
import com.cozycodr.ticket_support.client.dto.comment.SingleCommentResponse;
import com.cozycodr.ticket_support.client.dto.ticket.*;
import com.cozycodr.ticket_support.client.enums.TicketStatus;
import com.cozycodr.ticket_support.client.utils.AuthManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;
import java.util.function.Consumer;

@Service
@Slf4j
@RequiredArgsConstructor
public class ClientTicketService {

    private final ObjectMapper objectMapper;
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final AuthManager authManager;

    @Value("${app.api.base-url}")
    private String baseUrl;

    @Autowired
    public ClientTicketService (AuthManager authManager){
        this.authManager = authManager;
        this.objectMapper = ObjectMapperConfig.createObjectMapper();
    }

    /**
     * Fetch tickets created by a specific user.
     * <p>
     * This method calls the backend "/tickets/user" endpoint using the provided userId,
     * along with pagination parameters.
     *
     * @param page      The page number (starting from 1).
     * @param size      The number of tickets per page.
     * @param onSuccess Consumer that receives an array of TicketResponse objects on success.
     * @param onError   Consumer that receives an error message string if fetching fails.
     */
    public void fetchMyTickets(int page, int size, Consumer<TicketListResponse> onSuccess, Consumer<String> onError) {
        // Build the endpoint URL with pagination query parameters.
        String url = String.format("%s/tickets/user?page=%d&size=%d", baseUrl, page, size);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", authManager.getAuthHeader())
                .GET()
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    if(response.statusCode() == 200) {
                        try {
                            // response payload is wrapped in an ApiResponseBody,
                            // and the actual data is in a field "data".
                            TypeReference<ApiResponseBody<TicketDataResponse<TicketListResponse>>> ticketListTypeRef = new TypeReference<>() {};
                            ApiResponseBody<TicketDataResponse<TicketListResponse>> body = objectMapper.readValue(response.body(), ticketListTypeRef);

                            // Fetch Ticket List
                            TicketListResponse ticketList = body.getData().getTickets();
                            onSuccess.accept(ticketList);
                        } catch(IOException e) {
                            log.error("Error parsing tickets response", e);
                            onError.accept("Error parsing tickets response: " + e.getMessage());
                        }
                    } else {
                        onError.accept("Error fetching tickets: HTTP " + response.statusCode());
                    }
                })
                .exceptionally(e -> {
                    log.error("Exception during fetching tickets", e);
                    onError.accept("Exception during fetching tickets: " + e.getMessage());
                    return null;
                });
    }

    /**
     * Create a new ticket via the backend API.
     *
     * @param requestBody create ticket request.
     * @param onSuccess        Consumer with the response JSON on success.
     * @param onError          Consumer with an error message on failure.
     */
    public void createTicket(CreateTicketRequest requestBody, Consumer<TicketResponse> onSuccess, Consumer<String> onError) {
        String url = baseUrl + "/tickets";

        try {
            String requestJson = objectMapper.writeValueAsString(requestBody);


        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .header("Authorization", authManager.getAuthHeader())
                .POST(HttpRequest.BodyPublishers.ofString(requestJson))
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    if (response.statusCode() == 201) {
                        try {
                            TypeReference<ApiResponseBody<TicketResponse>> ticketTypeRef = new TypeReference<>() {};
                            ApiResponseBody<TicketResponse> body = objectMapper.readValue(response.body(), ticketTypeRef);

                            TicketResponse ticket = body.getData();
                            onSuccess.accept(ticket);
                        } catch (IOException e) {
                            log.error("Error parsing create ticket response", e);
                            onError.accept("Error parsing response: " + e.getMessage());
                        }
                    } else {
                        onError.accept("Error creating ticket: HTTP " + response.statusCode());
                    }
                })
                .exceptionally(e -> {
                    log.error("Exception during creating ticket", e);
                    onError.accept("Exception during creating ticket: " + e.getMessage());
                    return null;
                });
        } catch (JsonProcessingException e) {
            onError.accept("Error processing request: " + e.getMessage());
        }
    }


    /**
     * Fetch detailed information for a specific ticket.
     *
     * @param ticketId  The UUID of the ticket.
     * @param onSuccess Consumer that receives a TicketResponse object on success.
     * @param onError   Consumer that receives an error message string on failure.
     */
    public void fetchTicketDetails(UUID ticketId, Consumer<TicketResponse> onSuccess, Consumer<String> onError) {
        String url = String.format("%s/tickets/%s", baseUrl, ticketId);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", authManager.getAuthHeader())
                .GET()
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    if(response.statusCode() == 200) {
                        try {
                            TypeReference<ApiResponseBody<SingleTicketResponse>> ticketTypeRef = new TypeReference<>() {};
                            ApiResponseBody<SingleTicketResponse> body = objectMapper.readValue(response.body(), ticketTypeRef);

                            TicketResponse ticket = body.getData().getTicket();
                            onSuccess.accept(ticket);
                        } catch (IOException e) {
                            log.error("Error parsing ticket details", e);
                            onError.accept("Error parsing ticket details: " + e.getMessage());
                        }
                    } else {
                        onError.accept("Error fetching ticket details: HTTP " + response.statusCode());
                    }
                })
                .exceptionally(e -> {
                    log.error("Exception during fetching ticket details", e);
                    onError.accept("Exception during fetching ticket details: " + e.getMessage());
                    return null;
                });
    }

    /**
     * Fetch a paginated list of all tickets for IT Support.
     *
     * @param page      The page number (starting from 1).
     * @param size      The number of tickets per page.
     * @param onSuccess Consumer that receives a PageResponse of TicketResponse on success.
     * @param onError   Consumer that receives an error message on failure.
     */
    public void fetchAllTickets(int page, int size, Consumer<PageResponse<TicketResponse>> onSuccess, Consumer<String> onError) {
        String url = String.format("%s/tickets?page=%d&size=%d", baseUrl, page, size);

        HttpRequest request = HttpRequest.newBuilder()
                .header("Authorization", authManager.getAuthHeader())
                .uri(URI.create(url))
                .GET()
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    if(response.statusCode() == 200) {
                        try {
                            TypeReference<ApiResponseBody<TicketDataResponse<PageResponse<TicketResponse>>>> typeRef = new TypeReference<>(){};
                            ApiResponseBody<TicketDataResponse<PageResponse<TicketResponse>>> body = objectMapper.readValue(response.body(), typeRef);

                            PageResponse<TicketResponse> pageResponse = body.getData().getTickets();
                            onSuccess.accept(pageResponse);
                        } catch(IOException e) {
                            log.error("Error parsing tickets page response", e);
                            onError.accept("Error parsing tickets page response: " + e.getMessage());
                        }
                    } else {
                        onError.accept("Error fetching tickets: HTTP " + response.statusCode());
                    }
                })
                .exceptionally(e -> {
                    log.error("Exception during fetching all tickets", e);
                    onError.accept("Exception during fetching all tickets: " + e.getMessage());
                    return null;
                });
    }

    public void updateTicketStatus(UUID ticketId, TicketStatus newStatus,
                                   Consumer<TicketResponse> onSuccess,
                                   Consumer<String> onError) {
        try {
            UpdateTicketStatusRequest request = new UpdateTicketStatusRequest();
            request.setStatus(newStatus);

            String jsonBody = objectMapper.writeValueAsString(request);
            String url = String.format("%s/tickets/%s/status", baseUrl, ticketId);

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .header("Authorization", authManager.getAuthHeader())
                    .method("PATCH", HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(response -> {
                        if (response.statusCode() == 200) {
                            try {
                                TypeReference<ApiResponseBody<SingleTicketResponse>> typeRef =
                                        new TypeReference<>() {};
                                ApiResponseBody<SingleTicketResponse> body =
                                        objectMapper.readValue(response.body(), typeRef);

                                onSuccess.accept(body.getData().getTicket());
                            } catch (IOException e) {
                                log.error("Error parsing status update response", e);
                                onError.accept("Error parsing status update response: " + e.getMessage());
                            }
                        } else {
                            onError.accept("Error updating ticket status: HTTP " + response.statusCode());
                        }
                    })
                    .exceptionally(e -> {
                        log.error("Exception during status update", e);
                        onError.accept("Exception during status update: " + e.getMessage());
                        return null;
                    });

        } catch (Exception e) {
            log.error("Error preparing status update request", e);
            onError.accept("Error preparing status update request: " + e.getMessage());
        }
    }

    public void addComment(UUID ticketId, String comment,
                           Consumer<SingleCommentResponse> onSuccess,
                           Consumer<String> onError) {
        try {
            AddCommentRequest request = new AddCommentRequest();
            request.setMessage(comment);

            String jsonBody = objectMapper.writeValueAsString(request);
            String url = String.format("%s/tickets/%s/comments", baseUrl, ticketId);

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .header("Authorization", authManager.getAuthHeader())
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(response -> {
                        if (response.statusCode() == 201) {
                            try {
                                TypeReference<ApiResponseBody<SingleCommentResponse>> typeRef =
                                        new TypeReference<>() {};
                                ApiResponseBody<SingleCommentResponse> body =
                                        objectMapper.readValue(response.body(), typeRef);

                                onSuccess.accept(body.getData());
                            } catch (IOException e) {
                                log.error("Error parsing comment response", e);
                                onError.accept("Error parsing comment response: " + e.getMessage());
                            }
                        } else {
                            onError.accept("Error adding comment: HTTP " + response.statusCode());
                        }
                    })
                    .exceptionally(e -> {
                        log.error("Exception during comment addition", e);
                        onError.accept("Exception during comment addition: " + e.getMessage());
                        return null;
                    });

        } catch (Exception e) {
            log.error("Error preparing comment request", e);
            onError.accept("Error preparing comment request: " + e.getMessage());
        }
    }
}