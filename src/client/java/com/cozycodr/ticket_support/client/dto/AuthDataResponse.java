package com.cozycodr.ticket_support.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthDataResponse {
    private String token;
    private String username;
    private String role;

    public boolean isItSupport() {
        return "IT_SUPPORT".equals(role);
    }
}
