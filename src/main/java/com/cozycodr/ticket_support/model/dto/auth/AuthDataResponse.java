package com.cozycodr.ticket_support.model.dto.auth;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthDataResponse {
    private String token;
    private String username;
    private String role;
}
