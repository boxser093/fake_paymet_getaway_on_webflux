package com.ilya.payment_getaway.it;

import com.ilya.payment_getaway.config.PostgresTestContainerConfig;
import com.ilya.payment_getaway.entity.AccountMerchant;
import com.ilya.payment_getaway.entity.Currency;
import com.ilya.payment_getaway.entity.Merchant;
import com.ilya.payment_getaway.repository.AccountMerchantRepository;
import com.ilya.payment_getaway.repository.MerchantRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@Import(PostgresTestContainerConfig.class)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BalanceRestControllerV1TestIT {
    @Autowired
    private MerchantRepository merchantRepository;
    @Autowired
    private AccountMerchantRepository accountMerchantRepository;
    @Autowired
    private WebTestClient webTestClient;

    @BeforeEach
    public void setUp() {
        accountMerchantRepository.deleteAll().block();
        merchantRepository.deleteAll().block();
    }

    @Test
    @Order(1)
    @DisplayName("Get balance merchant when available")
    void getBalanceForMerchants_Successful() {
        //given
        String auth = "Basic VGVzdDpUZXN0";
        Merchant merchant = merchantRepository.save(Merchant.builder()
                .key("VGVzdA==")
                .merchantId("Test")
                .createAt(LocalDateTime.now())
                .build()).block();

        AccountMerchant accountMerchant = accountMerchantRepository.save(AccountMerchant.builder()
                .merchant(merchant)
                .merchantId(merchant.getId())
                .createAt(LocalDateTime.now())
                .currency(Currency.USD)
                .balance(BigDecimal.valueOf(200000))
                .build()).block();

        //when
        WebTestClient.ResponseSpec response = webTestClient
                .get()
                .uri("/api/v1/merchants/balance")
                .header("Authorization", auth)
                .exchange();
        //then
        response.expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.[0].id").isNotEmpty()
                .jsonPath("$.[0].balance").isEqualTo(accountMerchant.getBalance());
    }

    @Test
    @Order(2)
    @DisplayName("Get balance when merchant not available")
    void getBalanceForMerchants_Unsuccessful() {
        //given
        String auth = "Basic VGVzdDpUZXN0";

        //when
        WebTestClient.ResponseSpec response = webTestClient
                .get()
                .uri("/api/v1/merchants/balance")
                .header("Authorization", auth)
                .exchange();
        //then
        response
                .expectStatus().is5xxServerError();


    }
}
