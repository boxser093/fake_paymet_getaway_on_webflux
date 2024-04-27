package com.ilya.payment_getaway.config;

import com.ilya.payment_getaway.service.impl.TransactionStatusFinalizeSchedulerServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
@EnableAsync
@RequiredArgsConstructor
@ConditionalOnProperty(name = "scheduler.enabled", matchIfMissing = true)
public class SchedulerConfig {

    private final TransactionStatusFinalizeSchedulerServiceImpl transactionStatusService;



    @Scheduled(fixedRate = 300000)
    public void finalizeTransaction() {
        transactionStatusService.startFinalize();
    }


}
