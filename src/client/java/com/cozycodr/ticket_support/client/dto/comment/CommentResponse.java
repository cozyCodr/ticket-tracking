package com.cozycodr.ticket_support.client.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponse {

    private UUID id;
    private String message;
    private Commenter commenter;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}
