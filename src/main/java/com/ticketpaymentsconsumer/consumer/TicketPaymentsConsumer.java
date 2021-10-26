package com.ticketpaymentsconsumer.consumer;

import com.avro.ticketpayments.Ticket;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.ticketpaymentsconsumer.service.TicketPaymentsService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
@AllArgsConstructor
public class TicketPaymentsConsumer {

    private final TicketPaymentsService ticketPaymentsService;

    @KafkaListener(topics = {"ticket-payments"})
    public void onMessage(ConsumerRecord<Integer, Ticket> consumerRecord) throws IOException {

        log.info("Consumer Ticket : {}", consumerRecord.key());
        ticketPaymentsService.processTicketPayments(consumerRecord);
    }

}
