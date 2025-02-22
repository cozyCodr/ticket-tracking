package com.cozycodr.ticket_support.model.entity;

import com.cozycodr.ticket_support.model.enums.TicketCategory;
import com.cozycodr.ticket_support.model.enums.TicketPriority;
import com.cozycodr.ticket_support.model.enums.TicketStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(nullable = false)
    private TicketPriority priority;

    @Column(nullable = false)
    private TicketCategory category;

    @Builder.Default
    @Column(nullable = false)
    private TicketStatus status = TicketStatus.NEW;

    @JsonIgnore
    @Column(nullable = false)
    @ManyToOne(cascade = CascadeType.ALL)
    private User raisedBy;

    @JsonIgnore
    @Builder.Default
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @CreationTimestamp
    private LocalDateTime createdDate;

    @UpdateTimestamp
    private LocalDateTime updatedDate;
}
