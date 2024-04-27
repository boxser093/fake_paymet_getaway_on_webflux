package com.ilya.payment_getaway.it;

import com.ilya.payment_getaway.config.PostgresTestContainerConfig;
import com.ilya.payment_getaway.dto.TransactionDto;
import com.ilya.payment_getaway.entity.*;
import com.ilya.payment_getaway.mapper.TransactionMapper;
import com.ilya.payment_getaway.repository.*;
import com.ilya.payment_getaway.util.DateUtils;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@Import(PostgresTestContainerConfig.class)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TopUpRestControllerV1TestIT {
    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private TransactionMapper transactionMapper;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private AccountMerchantRepository accountMerchantRepository;
    @Autowired
    private CustomerCardRepository customerCardRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private MerchantRepository merchantRepository;

    @BeforeEach
    public void setUp() {
        transactionRepository.deleteAll().block();
        customerCardRepository.deleteAll().block();
        customerRepository.deleteAll().block();
        accountMerchantRepository.deleteAll().block();
        merchantRepository.deleteAll().block();
    }

    @Test
    @Order(1)
    @DisplayName("Successful Request when transaction available")
    void getTopUpTransactionById_Successful() {
//        given
        Merchant merchant = merchantRepository.save(Merchant.builder()
                .key("VGVzdA==")
                .merchantId("Test")
                .createAt(LocalDateTime.now())
                .build()).block();

        AccountMerchant account = accountMerchantRepository.save(AccountMerchant.builder()
                .merchant(merchant)
                .merchantId(merchant.getId())
                .createAt(LocalDateTime.now())
                .currency(Currency.USD)
                .balance(BigDecimal.valueOf(200000))
                .build()).block();

        String auth = "Basic VGVzdDpUZXN0";
        Transaction transactionSave = transactionRepository.save(DateUtils.getTransactionInProgressWithoutId().toBuilder()
                .accountId(account.getId())
                .build()).block();
        UUID id = transactionSave.getId();
        //when
        WebTestClient.ResponseSpec response = webTestClient
                .get()
                .uri("/api/v1/payments/transaction/{id}/details", id)
                .header(HttpHeaders.AUTHORIZATION, auth)
                .exchange();
        //then
        response.
                expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.transaction_type").isEqualTo(TransactionType.TOP_UP.name());
    }

    @Test
    @Order(2)
    @DisplayName("Unsuccessful Request with false id transaction")
    void getTopUpTransactionById_Unsuccessful() {
        //given
        Merchant merchant = merchantRepository.save(Merchant.builder()
                .key("VGVzdA==")
                .merchantId("Test")
                .createAt(LocalDateTime.now())
                .build()).block();
        AccountMerchant account = accountMerchantRepository.save(AccountMerchant.builder()
                .merchant(merchant)
                .merchantId(merchant.getId())
                .createAt(LocalDateTime.now())
                .currency(Currency.USD)
                .balance(BigDecimal.valueOf(200000))
                .build()).block();
        String auth = "Basic VGVzdDpUZXN0";
        UUID id = UUID.randomUUID();
        //when
        WebTestClient.ResponseSpec response = webTestClient
                .get()
                .uri("/api/v1/payments/transaction/{id}/details", id)
                .header("Authorization", auth)
                .exchange();
        //then
        response.
                expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").isNotEmpty();

    }

    @Test
    @Order(3)
    @DisplayName("Successful Request when Customer and CustomerCard available and positive balance")
    void createTransaction_Successful() {
        //given
        Merchant merchantJimCarrey = merchantRepository.save(DateUtils.getMerchantJimCarreyTransient()).block();
        AccountMerchant accountMerchantJimCarrey = accountMerchantRepository.save(DateUtils.getAccountMerchantJimCarreyTransient()
                .toBuilder()
                .merchant(merchantJimCarrey)
                .merchantId(merchantJimCarrey.getId())
                .build()).block();
        Customer customerJohnCena = customerRepository.save(DateUtils.getCustomerGutsBerserkTransient()).block();
        CustomerCard customerCardJohnCena = customerCardRepository.save(DateUtils.getCustomerCardGutsBerserkTransient()).block();

        String auth = "Basic VGVzdDpUZXN0";
        Transaction transactionTopUpInProgress = DateUtils.getTransactionForPostRequest();
        //when
        WebTestClient.ResponseSpec response = webTestClient
                .post()
                .uri("/api/v1/payments/transaction/")
                .header(HttpHeaders.AUTHORIZATION, auth)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(Mono.just(transactionMapper.map(transactionTopUpInProgress)), TransactionDto.class)
                .exchange();
        //then
        response.expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.transaction_code").isNotEmpty()
                .jsonPath("$.status").isEqualTo(TransactionStatus.IN_PROGRESS.name())
                .jsonPath("$.operation_status").isEqualTo("OK");
    }

    @Test
    @Order(4)
    @DisplayName("Unsuccessful Request when Customer and CustomerCard not available, balance ZERO")
    void createTransaction_Unsuccessful() {
        //given
        Merchant merchantJimCarrey = merchantRepository.save(DateUtils.getMerchantJimCarreyTransient()).block();
        AccountMerchant accountMerchantJimCarrey = accountMerchantRepository.save(DateUtils.getAccountMerchantJimCarreyTransient()
                .toBuilder()
                .merchant(merchantJimCarrey)
                .merchantId(merchantJimCarrey.getId())
                .build()).block();

        String auth = "Basic VGVzdDpUZXN0";
        Transaction transactionTopUpInProgress = DateUtils.getTransactionForPostRequestZeroBalance();
        //when
        WebTestClient.ResponseSpec response = webTestClient
                .post()
                .uri("/api/v1/payments/transaction/")
                .header(HttpHeaders.AUTHORIZATION, auth)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(Mono.just(transactionMapper.map(transactionTopUpInProgress)), TransactionDto.class)
                .exchange();
        //then
        response.expectStatus().isBadRequest()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.message").isNotEmpty();
    }

    @Test
    @Order(5)
    @DisplayName("Successful Request find transaction when available in BD")
    void getAllTopUpTransactionsWithOutDate() {
        //given
        Merchant merchantJimCarrey = merchantRepository.save(DateUtils.getMerchantJimCarreyTransient()).block();
        AccountMerchant accountMerchantJimCarrey = accountMerchantRepository.save(DateUtils.getAccountMerchantJimCarreyTransient()
                .toBuilder()
                .merchant(merchantJimCarrey)
                .merchantId(merchantJimCarrey.getId())
                .build()).block();
        String auth = "Basic VGVzdDpUZXN0";
        Long accountMerchantJimCarreyId = accountMerchantJimCarrey.getId();
        Transaction t1 = transactionRepository.save(DateUtils.getTransactionForFindAllList1().toBuilder()
                .accountId(accountMerchantJimCarreyId)
                .build()).block();
        Transaction t2 = transactionRepository.save(DateUtils.getTransactionForFindAllList2().toBuilder()
                .accountId(accountMerchantJimCarreyId)
                .build()).block();
        Transaction t3 = transactionRepository.save(DateUtils.getTransactionForFindAllList3().toBuilder()
                .accountId(accountMerchantJimCarreyId)
                .build()).block();

        //when
        WebTestClient.ResponseSpec response = webTestClient
                .get()
                .uri("/api/v1/payments/transaction/lists")
                .header(HttpHeaders.AUTHORIZATION, auth)
                .exchange();
        //then
        response.
                expectStatus().isOk()
                .expectBody()
                .jsonPath("$.[0].id").isEqualTo(t1.getId().toString())
                .jsonPath("$.[1].id").isEqualTo(t2.getId().toString())
                .jsonPath("$.[2].id").isEqualTo(t3.getId().toString());

    }

    @Test
    @Order(6)
    @DisplayName("Successful Request find transaction when available in BD for date pattern")
    void getAllTopUpTransactionsWithDate() {
        //given
        LocalDateTime before = LocalDateTime.now();
        Merchant merchantJimCarrey = merchantRepository.save(DateUtils.getMerchantJimCarreyTransient()).block();
        AccountMerchant accountMerchantJimCarrey = accountMerchantRepository.save(DateUtils.getAccountMerchantJimCarreyTransient()
                .toBuilder()
                .merchant(merchantJimCarrey)
                .merchantId(merchantJimCarrey.getId())
                .build()).block();
        String auth = "Basic VGVzdDpUZXN0";
        Long accountMerchantJimCarreyId = accountMerchantJimCarrey.getId();
        Transaction t1 = transactionRepository.save(DateUtils.getTransactionForFindAllList1().toBuilder()
                .accountId(accountMerchantJimCarreyId)
                .build()).block();
        Transaction t2 = transactionRepository.save(DateUtils.getTransactionForFindAllList2().toBuilder()
                .accountId(accountMerchantJimCarreyId)
                .build()).block();

        LocalDateTime after = LocalDateTime.now();
        Transaction t3 = transactionRepository.save(DateUtils.getTransactionForFindAllList3().toBuilder()
                .createAt(after)
                .accountId(accountMerchantJimCarreyId)
                .build()).block();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
        String formatBefore = before.format(formatter);
        String formatAfter = after.format(formatter);
        //when
        WebTestClient.ResponseSpec response = webTestClient
                .get()
                .uri("/api/v1/payments/transaction/lists?start_date=" +formatBefore+"&end_date="+formatAfter)
                .header(HttpHeaders.AUTHORIZATION, auth)
                .exchange();
        //then
        response.
                expectStatus().isOk()
                .expectBody().consumeWith(System.out::println)
                .jsonPath("$.[0].id").isEqualTo(t1.getId().toString())
                .jsonPath("$.[1].id").isEqualTo(t2.getId().toString())
                .jsonPath("$.length()").isEqualTo(2);

    }
}