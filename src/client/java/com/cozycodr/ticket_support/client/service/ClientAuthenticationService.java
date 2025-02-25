package com.cozycodr.ticket_support.client.service;

import com.cozycodr.ticket_support.client.dto.ApiResponseBody;
import com.cozycodr.ticket_support.client.dto.AuthDataResponse;
import com.cozycodr.ticket_support.client.dto.LoginRequest;
import com.cozycodr.ticket_support.client.dto.RegistrationRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.function.Consumer;

@Slf4j
@Service
public class ClientAuthenticationService {

    private final String apiBaseUrl;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public ClientAuthenticationService(
            @Value("${app.api.base-url}") String apiBaseUrl,
            ObjectMapper objectMapper
    ) {
        this.apiBaseUrl = apiBaseUrl;
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = objectMapper;
    }

    public void register(RegistrationRequest request,
                         Consumer<AuthDataResponse> onSuccess,
                         Consumer<String> onError) {
        try {
            String requestBody = objectMapper.writeValueAsString(request);

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(apiBaseUrl + "/auth/register"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(response -> {
                        if (response.statusCode() == 201) {
                            try {
                                // A type reference for the auth Response
                                TypeReference<ApiResponseBody<AuthDataResponse>> authDataTypeRef = new TypeReference<>(){};

                                ApiResponseBody<AuthDataResponse> authResponse = objectMapper.readValue(
                                        response.body(),
                                        authDataTypeRef
                                );
                                if (authResponse.getData() != null) {
                                    onSuccess.accept(authResponse.getData());
                                } else {
                                    log.error("No data in response");
                                    onError.accept("No data in response");
                                }
                            } catch (Exception e) {
                                log.error(e.getMessage());
                                onError.accept("Error parsing response: " + e.getMessage());
                            }
                        } else {
                            log.error(requestBody);
                            onError.accept("Registration failed");
                        }
                    })
                    .exceptionally(e -> {
                        log.error(e.getMessage());
                        onError.accept("Network error: " + e.getMessage());
                        return null;
                    });

        } catch (Exception e) {
            onError.accept("Error creating request: " + e.getMessage());
        }
    }

    public void login(LoginRequest request,
                      Consumer<AuthDataResponse> onSuccess,
                      Consumer<String> onError) {
        try {
            String requestBody = objectMapper.writeValueAsString(request);

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(apiBaseUrl + "/auth/login"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(response -> {
                        if (response.statusCode() == 200) {
                            try {
                                // A type reference for the auth Response
                                TypeReference<ApiResponseBody<AuthDataResponse>> authDataTypeRef = new TypeReference<>(){};

                                ApiResponseBody<AuthDataResponse> authResponse = objectMapper.readValue(
                                        response.body(),
                                        authDataTypeRef
                                );
                                if (authResponse.getData() != null) {
                                    onSuccess.accept(authResponse.getData());
                                } else {

                                    onError.accept(response.body());
                                }
                            } catch (Exception e) {
                                onError.accept("Error parsing response: " + e.getMessage());
                            }
                        } else {
                            onError.accept("Login failed, invalid username or password" );
                        }
                    })
                    .exceptionally(e -> {
                        onError.accept("Network error: " + e.getMessage());
                        return null;
                    });

        } catch (Exception e) {
            onError.accept(e.getMessage());
        }
    }
}
