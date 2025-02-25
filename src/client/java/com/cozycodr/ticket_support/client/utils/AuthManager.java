package com.cozycodr.ticket_support.client.utils;

import org.springframework.stereotype.Component;

@Component
public class AuthManager {
    private String authToken;

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getAuthToken() {
        return authToken;
    }

    /**
     * Convenience method to get a formatted auth header.
     * @return the "Bearer {token}" header value.
     */
    public String getAuthHeader() {
        return authToken != null ? "Bearer " + authToken : "";
    }
}
