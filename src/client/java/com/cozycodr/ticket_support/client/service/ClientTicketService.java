package com.cozycodr.ticket_support.client.service;

import com.cozycodr.ticket_support.client.config.ObjectMapperConfig;
import com.cozycodr.ticket_support.client.dto.ApiResponseBody;
import com.cozycodr.ticket_support.client.dto.PageResponse;
import com.cozycodr.ticket_support.client.dto.ticket.SingleTicketResponse;
import com.cozycodr.ticket_support.client.dto.ticket.TicketDataResponse;
import com.cozycodr.ticket_support.client.dto.ticket.TicketListResponse;
import com.cozycodr.ticket_support.client.dto.ticket.TicketResponse;
import com.cozycodr.ticket_support.client.utils.AuthManager;
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
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

@Service
@Slf4j
@RequiredArgsConstructor
public class ClientTicketService {

    private final ObjectMapper objectMapper;
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final AuthManager authManager;

    // Base URL for the API endpoints, e.g., "http://localhost:8080/api"
    @Value("${client.api.base-url}")
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
     * @return
     */
    public List<TicketResponse> fetchMyTickets(int page, int size, Consumer<TicketListResponse> onSuccess, Consumer<String> onError) {

        // todo: extract auth header
        String authHeader = "";

        // Build the endpoint URL with pagination query parameters.
        String url = String.format("%s/tickets/user?page=%d&size=%d", baseUrl, page, size);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                // Set the required header for user information.
                .header("Authorization", authHeader)
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
                            TicketListResponse ticketList = body.data().getTickets();
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
        return null;
    }

    /**
     * Create a new ticket via the backend API.
     *
     * @param createTicketJson JSON string representing the create ticket request.
     * @param userId           The UUID of the user creating the ticket.
     * @param onSuccess        Consumer with the response JSON on success.
     * @param onError          Consumer with an error message on failure.
     */
    public void createTicket(String createTicketJson, UUID userId, Consumer<TicketResponse> onSuccess, Consumer<String> onError) {
        String url = baseUrl + "/tickets";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .header("X-User-Id", userId.toString())
                .POST(HttpRequest.BodyPublishers.ofString(createTicketJson))
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    if (response.statusCode() == 201) {
                        try {
                            TypeReference<ApiResponseBody<TicketResponse>> ticketTypeRef = new TypeReference<>() {};
                            ApiResponseBody<TicketResponse> body = objectMapper.readValue(response.body(), ticketTypeRef);

                            TicketResponse ticket = body.data();
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
                .GET()
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    if(response.statusCode() == 200) {
                        try {
                            TypeReference<ApiResponseBody<SingleTicketResponse>> ticketTypeRef = new TypeReference<>() {};
                            ApiResponseBody<SingleTicketResponse> body = objectMapper.readValue(response.body(), ticketTypeRef);

                            TicketResponse ticket = body.data().getTicket();
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
                .uri(URI.create(url))
                .GET()
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    if(response.statusCode() == 200) {
                        try {
                            JsonNode rootNode = objectMapper.readTree(response.body());
                            JsonNode dataNode = rootNode.path("data");
                            // dataNode is a PageResponse structure.
                            PageResponse<TicketResponse> pageResponse = objectMapper.treeToValue(dataNode, objectMapper.getTypeFactory().constructParametricType(PageResponse.class, TicketResponse.class));
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
}