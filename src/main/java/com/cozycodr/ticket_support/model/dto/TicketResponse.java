package com.cozycodr.ticket_support.model.dto;

import com.cozycodr.ticket_support.model.enums.TicketCategory;
import com.cozycodr.ticket_support.model.enums.TicketPriority;
import com.cozycodr.ticket_support.model.enums.TicketStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class TicketResponse {
    private UUID id;
    private String title;
    private String description;
    private TicketStatus status;
    private TicketPriority priority;
    private TicketCategory category;
    private List<CommentResponse> last10Comments = new ArrayList<>();
    private String createdBy;
    private LocalDateTime createdDate;
}
