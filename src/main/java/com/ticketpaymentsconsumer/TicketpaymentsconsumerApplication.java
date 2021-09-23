package com.ticketpaymentsconsumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@ComponentScan("com.ticketpaymentsconsumer.*")
@EnableMongoRepositories(basePackages = "com.ticketpaymentsconsumer.repository")
public class TicketpaymentsconsumerApplication {

	public static void main(String[] args) {
		SpringApplication.run(TicketpaymentsconsumerApplication.class, args);
	}

}
