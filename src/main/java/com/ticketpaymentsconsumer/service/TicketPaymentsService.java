package com.ticketpaymentsconsumer.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ticketpaymentsconsumer.domain.Ticket;
import com.ticketpaymentsconsumer.repository.TicketPaymentsRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class TicketPaymentsService {

    private final TicketPaymentsRepository ticketPaymentsRepository;

    private final ObjectMapper objectMapper;

    public void processTicketPayments(ConsumerRecord<Integer, String> consumerRecord) throws JsonProcessingException {

        Ticket ticketRecivedKafka = objectMapper.readValue(consumerRecord.value(), Ticket.class);

        validation(ticketRecivedKafka);

        ticketRecivedKafka.calculateTotal();

        ticketPaymentsRepository.save(ticketRecivedKafka);

        log.info("Ticket : {}", ticketRecivedKafka);

    }

    private void validation(Ticket ticket){

        if(ticket.getAmount() == 0)
            throw new IllegalArgumentException("Tickets amount not valid");

        if(ticket.getAmount() >= 10)
            throw new RuntimeException("Amount cannot be more greater than 10 tickets");

        log.info("Tickets is valid!");
    }
}
