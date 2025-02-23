package com.cozycodr.ticket_support.client.service;

import com.cozycodr.ticket_support.client.config.ObjectMapperConfig;
import com.cozycodr.ticket_support.helpers.ApiResponseBody;
import com.cozycodr.ticket_support.model.dto.auth.AuthDataResponse;
import com.cozycodr.ticket_support.model.dto.auth.LoginRequest;
import com.cozycodr.ticket_support.model.dto.auth.RegistrationRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.function.Consumer;

@Slf4j
public class AuthenticationService {

    private static final String API_BASE_URL = "http://localhost:8080/api/v1/auth";
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public AuthenticationService() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = ObjectMapperConfig.createObjectMapper();
    }

    public void register(RegistrationRequest request,
                         Consumer<AuthDataResponse> onSuccess,
                         Consumer<String> onError) {
        try {
            String requestBody = objectMapper.writeValueAsString(request);

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE_URL + "/register"))
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
                                if (authResponse.data() != null) {
                                    onSuccess.accept(authResponse.data());
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
                            onError.accept("Registration failed: " + response.body());
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
                    .uri(URI.create(API_BASE_URL + "/login"))
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
                                if (authResponse.data() != null) {
                                    onSuccess.accept(authResponse.data());
                                } else {
                                    onError.accept("No data in response");
                                }
                            } catch (Exception e) {
                                onError.accept("Error parsing response: " + e.getMessage());
                            }
                        } else {
                            onError.accept("Login failed: " + response.body());
                        }
                    })
                    .exceptionally(e -> {
                        onError.accept("Network error: " + e.getMessage());
                        return null;
                    });

        } catch (Exception e) {
            onError.accept("Error creating request: " + e.getMessage());
        }
    }
}
