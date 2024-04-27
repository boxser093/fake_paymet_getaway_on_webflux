package com.ilya.payment_getaway.service.impl;

import com.ilya.payment_getaway.entity.*;
import com.ilya.payment_getaway.errorhandling.TransactionTopUtFailed;
import com.ilya.payment_getaway.errorhandling.TransactionWithdrawalFiled;
import com.ilya.payment_getaway.repository.TransactionRepository;
import com.ilya.payment_getaway.service.AccountService;
import com.ilya.payment_getaway.service.CustomerCardService;
import com.ilya.payment_getaway.service.CustomerService;
import com.ilya.payment_getaway.util.DateUtils;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TransactionServiceImplTest {
    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private CustomerCardService customerCardService;
    @Mock
    private CustomerService customerService;
    @Mock
    private AccountService accountService;
    @InjectMocks
    private TransactionServiceImpl transactionService;

    @Test
    @Order(1)
    void findById() {
        //given
        Transaction transactionWithdrawal1 = DateUtils.getTransactionWithdrawalInProgress();
        UUID id = transactionWithdrawal1.getId();

        //when
        when(transactionRepository.findById((UUID) any())).thenReturn(Mono.just(transactionWithdrawal1));
        //then
        StepVerifier
                .create(transactionService.findById(id))
                .expectNextMatches(transaction -> transaction.getId().equals(transactionWithdrawal1.getId())
                        && transaction.getTransactionStatus().equals(transactionWithdrawal1.getTransactionStatus()))
                .expectComplete()
                .verify();
    }

    @Test
    @Order(2)
    void findAllInProgress() {
        //given
        Transaction transaction = DateUtils.getTransactionWithdrawalInProgress();
        Transaction transactionTopUpInProgress = DateUtils.getTransactionTopUpInProgress();

        Flux<Transaction> allTransaction = Flux.just(transaction, transactionTopUpInProgress);
        //when
        when(transactionRepository.findAllByTransactionStatus(TransactionStatus.IN_PROGRESS)).thenReturn(allTransaction);
        //then
        StepVerifier
                .create(transactionService.findAllInProgress(TransactionStatus.IN_PROGRESS))
                .expectNext(transaction)
                .expectNext(transactionTopUpInProgress)
                .verifyComplete();
    }

    @Test
    @Order(3)
    void topUp_Successful() {
        //given
        UUID uuid = UUID.randomUUID();
        Transaction transactionTopUpSuccess = DateUtils.getTransactionTopUpInProgress().toBuilder()
                .id(uuid)
                .build();
        Merchant merchantJimCarrey = DateUtils.getMerchantJimCarrey();
        Customer customer = DateUtils.getCustomerJohnCena();
        CustomerCard customerCard = DateUtils.getCustomerCardJohnCena();
        CustomerCard customerCard1 = DateUtils.getCustomerCardJohnCena().toBuilder().balance(BigDecimal.ZERO).build();
        AccountMerchant accountMerchantJimCarrey = DateUtils.getAccountMerchantJimCarrey();
        System.out.println(customerCard.getBalance().equals(BigDecimal.ZERO));

        //when
        when(customerService.findCustomerByFirstNameAndLastName(anyString(), anyString())).thenReturn(Mono.just(customer));
        when(customerService.create(any())).thenReturn(Mono.just(customer));
        when(customerCardService.findCustomerCardByCartNumber(anyString())).thenReturn(Mono.just(customerCard));
        when(customerCardService.create(any())).thenReturn(Mono.just(customerCard1));
        when(accountService.findByMerchantIdAndCurrency(anyLong(), any(Currency.class))).thenReturn(Mono.just(accountMerchantJimCarrey));
        when(customerCardService.update(any(CustomerCard.class))).thenReturn(Mono.just(customerCard));
        when(accountService.update(any(AccountMerchant.class))).thenReturn(Mono.just(accountMerchantJimCarrey));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(Mono.just(transactionTopUpSuccess));

        //then
        StepVerifier
                .create(transactionService.topUp(transactionTopUpSuccess, merchantJimCarrey))
                .expectNextMatches(acceptResponse1 ->
                        acceptResponse1.getOPERATION_STATUS().equalsIgnoreCase("ok")
                                && acceptResponse1.getStatus().equals(TransactionStatus.IN_PROGRESS)
                                && acceptResponse1.getTransactionCode().equals(transactionTopUpSuccess.getId()))
                .expectComplete()
                .verify();
    }

    @Test
    @Order(4)
    void topUp_Unsuccessful() {
        //given
        UUID uuid = UUID.randomUUID();
        Transaction transactionTopUpSuccess = DateUtils.getTransactionTopUpInProgress().toBuilder()
                .id(uuid)
                .build();
        Merchant merchantJimCarrey = DateUtils.getMerchantJimCarrey();
        Customer customer = DateUtils.getCustomerJohnCena();
        CustomerCard customerCard1 = DateUtils.getCustomerCardJohnCena().toBuilder().balance(BigDecimal.ZERO).build();
        AccountMerchant accountMerchantJimCarrey = DateUtils.getAccountMerchantJimCarrey();

        //when
        when(customerService.findCustomerByFirstNameAndLastName(anyString(), anyString())).thenReturn(Mono.just(customer));
        when(customerService.create(any())).thenReturn(Mono.just(customer));
        when(customerCardService.findCustomerCardByCartNumber(anyString())).thenReturn(Mono.just(customerCard1));
        when(customerCardService.create(any())).thenReturn(Mono.just(customerCard1));
        when(accountService.findByMerchantIdAndCurrency(anyLong(), any(Currency.class))).thenReturn(Mono.just(accountMerchantJimCarrey));

        //then
        StepVerifier
                .create(transactionService.topUp(transactionTopUpSuccess, merchantJimCarrey))
                .expectError(TransactionTopUtFailed.class)
                .verify();
    }

    @Test
    @Order(5)
    void payout_Successful() {
        //given
        UUID uuid = UUID.randomUUID();
        Transaction transactionTopUpSuccess = DateUtils.getTransactionTopUpInProgress().toBuilder()
                .id(uuid)
                .build();
        Merchant merchantJimCarrey = DateUtils.getMerchantJimCarrey();
        Customer customer = DateUtils.getCustomerJohnCena();
        CustomerCard customerCard = DateUtils.getCustomerCardJohnCena();
        CustomerCard customerCard1 = DateUtils.getCustomerCardJohnCena().toBuilder().balance(BigDecimal.ZERO).build();
        AccountMerchant accountMerchantJimCarrey = DateUtils.getAccountMerchantJimCarrey();

        //when
        when(customerService.findCustomerByFirstNameAndLastName(anyString(), anyString())).thenReturn(Mono.just(customer));
        when(customerService.create(any())).thenReturn(Mono.just(customer));
        when(customerCardService.findCustomerCardByCartNumber(anyString())).thenReturn(Mono.just(customerCard));
        when(customerCardService.create(any())).thenReturn(Mono.just(customerCard1));
        when(accountService.findByMerchantIdAndCurrency(anyLong(), any(Currency.class))).thenReturn(Mono.just(accountMerchantJimCarrey));
        when(customerCardService.update(any(CustomerCard.class))).thenReturn(Mono.just(customerCard));
        when(accountService.update(any(AccountMerchant.class))).thenReturn(Mono.just(accountMerchantJimCarrey));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(Mono.just(transactionTopUpSuccess));

        //then
        StepVerifier
                .create(transactionService.payout(transactionTopUpSuccess, merchantJimCarrey))
                .expectNextMatches(acceptResponse1 ->
                        acceptResponse1.getOPERATION_STATUS().equalsIgnoreCase("ok")
                                && acceptResponse1.getStatus().equals(TransactionStatus.IN_PROGRESS)
                                && acceptResponse1.getTransactionCode().equals(transactionTopUpSuccess.getId()))
                .expectComplete()
                .verify();
    }

    @Test
    @Order(6)
    void payout_Unsuccessful() {
        //given
        UUID uuid = UUID.randomUUID();
        Transaction transactionTopUpSuccess = DateUtils.getTransactionTopUpInProgress().toBuilder()
                .id(uuid)
                .build();
        Merchant merchantJimCarrey = DateUtils.getMerchantJimCarrey();
        Customer customer = DateUtils.getCustomerJohnCena();
        CustomerCard customerCard1 = DateUtils.getCustomerCardJohnCena();
        AccountMerchant accountMerchantJimCarrey = DateUtils.getAccountMerchantJimCarrey().toBuilder()
                .balance(BigDecimal.TEN)
                .build();

        //when
        when(customerService.findCustomerByFirstNameAndLastName(anyString(), anyString())).thenReturn(Mono.just(customer));
        when(customerService.create(any())).thenReturn(Mono.just(customer));
        when(customerCardService.findCustomerCardByCartNumber(anyString())).thenReturn(Mono.just(customerCard1));
        when(customerCardService.create(any())).thenReturn(Mono.just(customerCard1));
        when(accountService.findByMerchantIdAndCurrency(anyLong(), any(Currency.class))).thenReturn(Mono.just(accountMerchantJimCarrey));

        //then
        StepVerifier
                .create(transactionService.payout(transactionTopUpSuccess, merchantJimCarrey))
                .expectError(TransactionWithdrawalFiled.class)
                .verify();
    }

    @Test
    @Order(7)
    void create() {
        UUID uuid = UUID.randomUUID();
        Transaction before = Transaction.builder()
                .transactionType(TransactionType.TOP_UP)
                .transactionStatus(TransactionStatus.IN_PROGRESS)
                .paymentMethod(PaymentMethod.CARD)
                .amount(BigDecimal.valueOf(1999))
                .currency(Currency.USD)
                .customerCardId(1L)
                .accountId(1L)
                .notificationUrl("https://foo.com")
                .build();

        Transaction after = Transaction.builder()
                .id(uuid)
                .transactionType(TransactionType.TOP_UP)
                .transactionStatus(TransactionStatus.IN_PROGRESS)
                .paymentMethod(PaymentMethod.CARD)
                .amount(BigDecimal.valueOf(1999))
                .currency(Currency.USD)
                .customerCardId(1L)
                .accountId(1L)
                .notificationUrl("https://foo.com")
                .build();

        //when
        when(transactionRepository.save(any(Transaction.class)))
                .thenReturn(Mono.just(after));
        //then
        StepVerifier
                .create(transactionService.create(before))
                .expectNextMatches(transaction -> transaction.getId().equals(uuid)
                        && transaction.getTransactionType().equals(after.getTransactionType()))
                .expectComplete()
                .verify();
    }

    @Test
    @Order(8)
    void update() {
        //given
        UUID uuid = UUID.randomUUID();
        Transaction beforeUpdate = Transaction.builder()
                .transactionType(TransactionType.TOP_UP)
                .transactionStatus(TransactionStatus.IN_PROGRESS)
                .paymentMethod(PaymentMethod.CARD)
                .amount(BigDecimal.valueOf(1999))
                .currency(Currency.USD)
                .customerCardId(1L)
                .accountId(1L)
                .notificationUrl("https://foo.com")
                .build();

        Transaction afterUpdate = Transaction.builder()
                .id(uuid)
                .transactionType(TransactionType.TOP_UP)
                .transactionStatus(TransactionStatus.IN_PROGRESS)
                .paymentMethod(PaymentMethod.CARD)
                .amount(BigDecimal.valueOf(1999))
                .currency(Currency.USD)
                .customerCardId(1L)
                .accountId(1L)
                .notificationUrl("TEST")
                .build();
        //when
        when(transactionRepository.findById(beforeUpdate.getId())).thenReturn(Mono.just(beforeUpdate));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(Mono.just(afterUpdate));
        //then
        StepVerifier
                .create(transactionService.update(beforeUpdate))
                .expectNextMatches(transaction -> transaction.getId().equals(uuid)
                        && transaction.getAccountId().equals(afterUpdate.getAccountId())
                        && transaction.getNotificationUrl().equals("TEST"))
                .expectComplete()
                .verify();
    }

    @Test
    @Order(9)
    void findAll() {
        //given
        Transaction transaction = DateUtils.getTransactionWithdrawalInProgress();
        Transaction transactionTopUpInProgress = DateUtils.getTransactionTopUpInProgress();
        Transaction transactionTopUpSuccess = DateUtils.getTransactionTopUpSuccess();
        Transaction transactionTopUpFiled = DateUtils.getTransactionTopUpFiled();

        Flux<Transaction> allTransaction = Flux.just(transaction, transactionTopUpInProgress, transactionTopUpSuccess, transactionTopUpFiled);
        //when
        when(transactionRepository.findAll()).thenReturn(allTransaction);
        //then
        StepVerifier
                .create(transactionService.findAll())
                .expectNext(transaction)
                .expectNext(transactionTopUpInProgress)
                .expectNext(transactionTopUpSuccess)
                .expectNext(transactionTopUpFiled)
                .verifyComplete();
    }

    @Test
    @Order(10)
    void findAllByDateAndAccountId() {
        //given
        LocalDateTime now = LocalDateTime.now();
        Merchant merchantJimCarrey = DateUtils.getMerchantJimCarrey();
        AccountMerchant accountMerchantJimCarrey = DateUtils.getAccountMerchantJimCarrey();
        Transaction transaction = DateUtils.getTransactionWithdrawalInProgress().toBuilder()
                .createAt(now.plusSeconds(1))
                .accountId(accountMerchantJimCarrey.getId())
                .build();
        Transaction transaction1 = DateUtils.getTransactionTopUpInProgress().toBuilder()
                .createAt(now.plusSeconds(1))
                .accountId(accountMerchantJimCarrey.getId())
                .build();

        LocalDateTime before = LocalDateTime.now().plusMinutes(1);
        Transaction transaction2 = DateUtils.getTransactionInProgressWithoutId().toBuilder()
                .id(UUID.randomUUID())
                .createAt(LocalDateTime.now().plusMinutes(3))
                .build();
        System.out.println(now);
        Stream.of(transaction, transaction1, transaction2).forEach(System.out::println);
        System.out.println(before);
        Flux<Transaction> allTransaction = Flux.just(transaction, transaction1, transaction2);
        //when
        when(accountService.findAllByMerchantId(any())).thenReturn(Flux.just(accountMerchantJimCarrey));
        when(transactionRepository.findAllByAccountId(any())).thenReturn(allTransaction);
        //then
        StepVerifier
                .create(transactionService.findAllByDateAndAccountId(merchantJimCarrey, now.toString(),
                        before.toString(), TransactionType.TOP_UP))
                .expectNext(transaction)
                .expectNext(transaction1)
                .verifyComplete();
    }

    @Test
    @Order(11)
    void holdBalanceForTopUp_Successful() {
        //given
        UUID uuid = UUID.randomUUID();
        Transaction transactionTopUpInProgress = DateUtils.getTransactionTopUpInProgress();
        AccountMerchant accountMerchantJimCarrey = DateUtils.getAccountMerchantJimCarrey();
        CustomerCard customerCardGutsBerserk = DateUtils.getCustomerCardGutsBerserk();
        transactionTopUpInProgress.setId(uuid);
        //when
        when(customerCardService.update(any(CustomerCard.class))).thenReturn(Mono.just(customerCardGutsBerserk.toBuilder()
                .balance(customerCardGutsBerserk.getBalance().subtract(transactionTopUpInProgress.getAmount()))
                .build()));
        when(accountService.update(any(AccountMerchant.class))).thenReturn(Mono.just(accountMerchantJimCarrey.toBuilder()
                .balance(accountMerchantJimCarrey.getBalance().add(transactionTopUpInProgress.getAmount()))
                .build()));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(Mono.just(transactionTopUpInProgress));
        //then
        StepVerifier.create(transactionService.holdBalanceForTopUp(transactionTopUpInProgress, accountMerchantJimCarrey, customerCardGutsBerserk))
                .expectNextMatches(acceptResponse -> acceptResponse.getTransactionCode().equals(uuid)
                        && acceptResponse.getStatus().equals(TransactionStatus.IN_PROGRESS))
                .expectComplete()
                .verify();
    }

    @Test
    @Order(12)
    void holdBalanceForTopUp_Unsuccessful() {
        //given
        UUID uuid = UUID.randomUUID();
        Transaction transactionTopUpInProgress = DateUtils.getTransactionTopUpInProgress();
        AccountMerchant accountMerchantJimCarrey = DateUtils.getAccountMerchantJimCarrey();
        CustomerCard customerCardGutsBerserk = DateUtils.getCustomerCardGutsBerserk();
        transactionTopUpInProgress.setId(uuid);
        customerCardGutsBerserk.setBalance(BigDecimal.valueOf(transactionTopUpInProgress.getAmount().longValue() - 100));
        //then
        StepVerifier.create(transactionService.holdBalanceForTopUp(transactionTopUpInProgress, accountMerchantJimCarrey, customerCardGutsBerserk))
                .expectError(TransactionTopUtFailed.class)
                .verify();
    }

    @Test
    @Order(13)
    void holdBalanceForPayout_Successful() {
        UUID uuid = UUID.randomUUID();
        Transaction transactionTopUpInProgress = DateUtils.getTransactionPayoutInProgress();
        AccountMerchant accountMerchantJimCarrey = DateUtils.getAccountMerchantJimCarrey();
        CustomerCard customerCardGutsBerserk = DateUtils.getCustomerCardGutsBerserk();
        transactionTopUpInProgress.setId(uuid);
        //when
        when(customerCardService.update(any(CustomerCard.class))).thenReturn(Mono.just(customerCardGutsBerserk.toBuilder()
                .balance(customerCardGutsBerserk.getBalance().add(transactionTopUpInProgress.getAmount()))
                .build()));
        when(accountService.update(any(AccountMerchant.class))).thenReturn(Mono.just(accountMerchantJimCarrey.toBuilder()
                .balance(accountMerchantJimCarrey.getBalance().subtract(transactionTopUpInProgress.getAmount()))
                .build()));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(Mono.just(transactionTopUpInProgress));
        //then
        StepVerifier.create(transactionService.holdBalanceForPayout(transactionTopUpInProgress, accountMerchantJimCarrey, customerCardGutsBerserk))
                .expectNextMatches(acceptResponse -> acceptResponse.getTransactionCode().equals(uuid)
                        && acceptResponse.getStatus().equals(TransactionStatus.IN_PROGRESS))
                .expectComplete()
                .verify();
    }

    @Test
    @Order(14)
    void holdBalanceForPayout_Unsuccessful() {
        UUID uuid = UUID.randomUUID();
        Transaction transactionTopUpInProgress = DateUtils.getTransactionPayoutInProgress();
        AccountMerchant accountMerchantJimCarrey = DateUtils.getAccountMerchantJimCarrey();
        accountMerchantJimCarrey.setBalance(BigDecimal.valueOf(transactionTopUpInProgress.getAmount().longValue() - 100));
        System.out.println(accountMerchantJimCarrey.getBalance());
        System.out.println(accountMerchantJimCarrey.getBalance().compareTo(transactionTopUpInProgress.getAmount()));
        CustomerCard customerCardGutsBerserk = DateUtils.getCustomerCardGutsBerserk();
        transactionTopUpInProgress.setId(uuid);
        //when

        //then
        StepVerifier.create(transactionService.holdBalanceForPayout(transactionTopUpInProgress, accountMerchantJimCarrey, customerCardGutsBerserk))
                .expectError(TransactionWithdrawalFiled.class)
                .verify();
    }

    @Test
    @Order(15)
    void findTransactionByIdAndMerchantId() {
        Transaction transactionTopUpFiled = DateUtils.getTransactionTopUpFiled();
        UUID id = transactionTopUpFiled.getId();
        TransactionType transactionType = transactionTopUpFiled.getTransactionType();
        AccountMerchant accountMerchantJimCarrey = DateUtils.getAccountMerchantJimCarrey();
        //when
        when(accountService.findAllByMerchantId(any())).thenReturn(Flux.just(accountMerchantJimCarrey));
        when(transactionRepository.findAllByAccountId(1L)).thenReturn(Flux.just(transactionTopUpFiled));
        //then
        StepVerifier
                .create(transactionService.findTransactionByIdAndMerchantId(1L, id, transactionType))
                .expectNextMatches(transaction -> transaction.getId().equals(transactionTopUpFiled.getId())
                        && transaction.getTransactionStatus().equals(transactionTopUpFiled.getTransactionStatus()))
                .expectComplete()
                .verify();
    }
}

