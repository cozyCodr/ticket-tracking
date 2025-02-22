package com.cozycodr.ticket_support.repository;

import com.cozycodr.ticket_support.model.entity.Ticket;
import com.cozycodr.ticket_support.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, UUID> {

    // Method to find all tickets with pagination and sorting
    @Query("SELECT t FROM Ticket t ORDER BY t.createdDate DESC")
    Page<Ticket> findAllTicketsOrderByCreatedDateDesc(Pageable pageable);


    // Optional: Find tickets by creator with pagination
    @Query("SELECT t FROM Ticket t WHERE t.raisedBy = :createdBy ORDER BY t.createdDate DESC")
    Page<Ticket> findTicketsByRaisedBy(User user, Pageable pageable);
}
