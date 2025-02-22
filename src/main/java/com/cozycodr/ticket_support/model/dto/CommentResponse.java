package com.cozycodr.ticket_support.model.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class CommentResponse {

    private UUID id;
    private String message;
    private Commenter commenter;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}
