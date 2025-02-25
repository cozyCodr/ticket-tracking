package com.cozycodr.ticket_support.client.dto.comment;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class CommentResponse {

    private UUID id;
    private String message;
    private Commenter commenter;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}
