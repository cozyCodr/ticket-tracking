package com.cozycodr.ticket_support.client.dto.comment;

import lombok.Data;

import java.util.UUID;

@Data
public class Commenter {
    private UUID id;
    private String username;
}
