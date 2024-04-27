package com.ilya.payment_getaway.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.ilya.payment_getaway.entity.CustomerCard;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "Transaction Dto")
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CustomerDto {
    @Id
    private Long id;
    private String firstName;
    private String lastName;
    private String country;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    private List<CustomerCard> customerCardList;
}
