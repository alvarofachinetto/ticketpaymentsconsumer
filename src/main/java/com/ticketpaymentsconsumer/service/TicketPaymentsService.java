package com.ticketpaymentsconsumer.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ticketpaymentsconsumer.domain.Address;
import com.ticketpaymentsconsumer.domain.Buyer;
import com.ticketpaymentsconsumer.domain.Payment;
import com.ticketpaymentsconsumer.domain.Ticket;
import com.ticketpaymentsconsumer.repository.TicketPaymentsRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.io.JsonEncoder;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Service;
import tech.allegro.schema.json2avro.converter.JsonAvroConverter;

import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@AllArgsConstructor
public class TicketPaymentsService {

    private final TicketPaymentsRepository ticketPaymentsRepository;

    private final ObjectMapper objectMapper;

    public void processTicketPayments(ConsumerRecord<Integer, com.avro.ticketpayments.Ticket> consumerRecord) throws IOException {
        JsonAvroConverter jsonAvroConverter = new JsonAvroConverter();

        GenericRecord genericRecord = consumerRecord.value();

        byte[] ticketJs = jsonAvroConverter.convertToJson(genericRecord);

        Ticket ticket = objectMapper.readValue(ticketJs, Ticket.class);

        String addressValue = genericRecord.get("Address").toString();
        String buyerValue = genericRecord.get("Buyer").toString();
        String paymentValue = genericRecord.get("Payment").toString();

        Address address = objectMapper.readValue(addressValue, Address.class);
        Buyer buyer = objectMapper.readValue(buyerValue, Buyer.class);
        Payment payment = Payment.valueOf(paymentValue);

        ticket.setAddress(address);
        ticket.setBuyer(buyer);
        ticket.setPayment(payment);

        validation(ticket);

        ticket.setTotal(ticket.calculateTotal());

        ticketPaymentsRepository.save(ticket);

        log.info("Ticket : {}", ticket);

    }

    private void validation(Ticket ticket){

        if(ticket.getAmount() == 0)
            throw new IllegalArgumentException("Tickets amount not valid");

        if(ticket.getAmount() >= 10)
            throw new RuntimeException("Amount cannot be more greater than 10 tickets");

        log.info("Tickets is valid!");
    }

}
