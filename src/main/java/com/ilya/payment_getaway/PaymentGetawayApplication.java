package com.ilya.payment_getaway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.reactive.config.EnableWebFlux;


@SpringBootApplication
public class PaymentGetawayApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaymentGetawayApplication.class, args);

    }

}
