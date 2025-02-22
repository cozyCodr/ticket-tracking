package com.cozycodr.ticket_support.model.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class Commenter {
    private UUID id;
    private String username;
}
