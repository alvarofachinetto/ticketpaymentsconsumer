package com.ticketpaymentsconsumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ticketpaymentsconsumer.consumer.TicketPaymentsConsumer;
import com.ticketpaymentsconsumer.domain.Address;
import com.ticketpaymentsconsumer.domain.Buyer;
import com.ticketpaymentsconsumer.domain.Payment;
import com.ticketpaymentsconsumer.domain.Ticket;
import com.ticketpaymentsconsumer.repository.TicketPaymentsRepository;
import com.ticketpaymentsconsumer.service.TicketPaymentsService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@SpringBootTest(classes = TicketpaymentsconsumerApplication.class)
@EmbeddedKafka(topics = {"ticket-payments"}, partitions = 1)
@TestPropertySource(properties = {"spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}"
, "spring.kafka.consumer.bootstrap-servers=${spring.embedded.kafka.brokers}"})
class TicketPaymentsIntegrationTest {


    @Autowired
    EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    KafkaTemplate<Integer, String> kafkaTemplate;

    @Autowired
    KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    @SpyBean
    TicketPaymentsConsumer ticketPaymentsConsumer;

    @SpyBean
    TicketPaymentsService ticketPaymentsService;

    @Autowired
    TicketPaymentsRepository ticketPaymentsRepository;

    @Autowired
    ObjectMapper objectMapper;


    @BeforeEach
    void setup(){
        for(MessageListenerContainer messageListenerContainer : kafkaListenerEndpointRegistry.getListenerContainers()){
            ContainerTestUtils.waitForAssignment(messageListenerContainer, embeddedKafkaBroker.getPartitionsPerTopic());
        }
    }

    @AfterEach
    void tearDown(){
        ticketPaymentsRepository.deleteAll();
    }

    @Test
    void postNewTicketPayment() throws JsonProcessingException, ExecutionException, InterruptedException {
        Address address = Address.builder().
                street("Av Jucelino Kubistcheck")
                .number(25414)
                .build();

        Buyer buyer = Buyer.builder()
                .name("Álvaro Silva")
                .cpf("980.744.640-67")
                .email("alvaro.silva@gmail.com")
                .build();

        Ticket ticket = Ticket.builder()
                .title("Cars 4")
                .dateTime(LocalDateTime.of(2021,11,27,20,15))
                .address(address)
                .buyer(buyer)
                .amount(1)
                .price(new BigDecimal(15.35))
                .payment(Payment.PAYPAL)
                .build();

        String ticketJson = objectMapper.writeValueAsString(ticket);

        kafkaTemplate.sendDefault(ticketJson).get();

        CountDownLatch countDownLatch = new CountDownLatch(1);

        countDownLatch.await(3, TimeUnit.SECONDS);

        verify(ticketPaymentsConsumer, times(1)).onMessage(isA(ConsumerRecord.class));
        verify(ticketPaymentsService, times(1)).processTicketPayments(isA(ConsumerRecord.class));

        List<Ticket> ticketList = ticketPaymentsRepository.findAll();
        assertEquals(1, ticketList.size());

        ticketList.forEach(ticket1 -> {
            assertNotNull(ticket1.getTicketId());
            assertNotNull(ticket1.getTotal());
        });
    }


}
