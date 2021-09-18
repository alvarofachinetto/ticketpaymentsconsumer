package com.ticketpaymentsconsumer.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.AcknowledgingMessageListener;
import org.springframework.kafka.support.Acknowledgment;

//@Slf4j
//public class LibraryEventsConsumerManualOffset implements AcknowledgingMessageListener<Integer, String> {
//
//    @Override
//    public void onMessage(ConsumerRecord<Integer, String> data, Acknowledgment acknowledgment) {
//        log.info("ConsumerRecord : {} ", data );
//        acknowledgment.acknowledge();
//    }
//}
