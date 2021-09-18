package com.ticketpaymentsconsumer.repository;

import com.ticketpaymentsconsumer.domain.Ticket;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketPaymentsRepository extends MongoRepository<Ticket, String> {
}
