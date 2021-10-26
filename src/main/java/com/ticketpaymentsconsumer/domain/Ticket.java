package com.ticketpaymentsconsumer.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "tickets")
public class Ticket {

    @Id
    private String ticketId;

    @NotEmpty
    @NotNull
    private String title;

    @NotNull
    private Address address;

    @NotNull
    private Buyer buyer;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String dateTime;

    @NotNull
    private Integer amount;

    @NotNull
    private Double price;

    @NotNull
    private BigDecimal total;

    @NotNull
    private Payment payment;

    public BigDecimal calculateTotal(){
        return new BigDecimal(amount).multiply(new BigDecimal(price));
    }
}
