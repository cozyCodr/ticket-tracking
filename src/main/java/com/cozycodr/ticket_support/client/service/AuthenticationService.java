package com.cozycodr.ticket_support.client.service;

import com.cozycodr.ticket_support.model.dto.AuthDataResponse;
import com.cozycodr.ticket_support.model.dto.LoginRequest;
import com.cozycodr.ticket_support.model.dto.RegistrationRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.function.Consumer;

public class AuthenticationService {

    private static final String API_BASE_URL = "http://localhost:8080/auth";
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public AuthenticationService() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
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
                        if (response.statusCode() == 200) {
                            try {
                                AuthDataResponse authResponse = objectMapper.readValue(
                                        response.body(),
                                        AuthDataResponse.class
                                );
                                onSuccess.accept(authResponse);
                            } catch (Exception e) {
                                onError.accept("Error parsing response: " + e.getMessage());
                            }
                        } else {
                            onError.accept("Registration failed: " + response.body());
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
                                AuthDataResponse authResponse = objectMapper.readValue(
                                        response.body(),
                                        AuthDataResponse.class
                                );
                                onSuccess.accept(authResponse);
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
