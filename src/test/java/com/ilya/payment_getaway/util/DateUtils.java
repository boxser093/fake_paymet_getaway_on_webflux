package com.ilya.payment_getaway.util;

import com.ilya.payment_getaway.dto.*;
import com.ilya.payment_getaway.entity.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class DateUtils {
    public static AccountDto getAccountDtoJimCarrey() {
        return AccountDto.builder()
                .id(1L)
                .merchantId(1L)
                .balance(BigDecimal.valueOf(125000))
                .currency(Currency.USD)
                .build();
    }

    public static AccountMerchant getAccountMerchantJimCarrey() {
        return AccountMerchant.builder()
                .id(1L)
                .merchantId(1L)
                .balance(BigDecimal.valueOf(125000))
                .currency(Currency.USD)
                .build();
    }

    public static AccountMerchant getAccountMerchantJimCarreyTransient() {
        return AccountMerchant.builder()
                .balance(BigDecimal.valueOf(125000))
                .currency(Currency.USD)
                .build();
    }

    public static AccountDto getAccountDtoRussellCrowe() {
        return AccountDto.builder()
                .id(2L)
                .merchantId(2L)
                .balance(BigDecimal.valueOf(143000))
                .currency(Currency.USD)
                .build();
    }

    public static AccountMerchant getAccountMerchantRussellCrowe() {
        return AccountMerchant.builder()
                .id(2L)
                .merchantId(2L)
                .balance(BigDecimal.valueOf(143000))
                .currency(Currency.USD)
                .build();
    }

    public static MerchantDto getMerchantDtoJimCarrey() {
        return MerchantDto.builder()
                .id(1L)
                .key("")
                .accountMerchants(List.of())
                .build();
    }

    public static Merchant getMerchantJimCarrey() {
        return Merchant.builder()
                .id(1L)
                .key("Test")
                .merchantId("Test")
                .accountMerchants(List.of())
                .build();
    }

    public static Merchant getMerchantJimCarreyTransient() {
        return Merchant.builder()
                .key("VGVzdA==")
                .merchantId("Test")
                .accountMerchants(List.of())
                .build();
    }

    public static CustomerDto getCustomerDtoGutsBerserk() {
        return CustomerDto.builder()
                .id(1L)
                .country("Japan")
                .firstName("Guts")
                .lastName("Berserk")
                .customerCardList(List.of())
                .build();
    }

    public static Customer getCustomerGutsBerserk() {
        return Customer.builder()
                .id(1L)
                .country("Japan")
                .firstName("Guts")
                .lastName("Berserk")
                .customerCardList(List.of())
                .build();
    }

    public static Customer getCustomerGutsBerserkZeroBalance() {
        return Customer.builder()
                .country("Japan")
                .firstName("Guts")
                .lastName("Berserk")
                .customerCardList(List.of())
                .build();
    }

    public static Customer getCustomerGutsBerserkTransient() {
        return Customer.builder()
                .country("Japan")
                .firstName("Guts")
                .lastName("Berserk")
                .customerCardList(List.of())
                .build();
    }

    public static CustomerDto getCustomerDtoJohnCena() {
        return CustomerDto.builder()
                .id(2L)
                .country("USA")
                .firstName("John")
                .lastName("Cena")
                .customerCardList(List.of())
                .build();
    }

    public static Customer getCustomerJohnCena() {
        return Customer.builder()
                .id(2L)
                .country("USA")
                .firstName("John")
                .lastName("Cena")
                .customerCardList(List.of())
                .build();
    }

    public static CustomerCardDto getCustomerCardGutsBerserkDto() {
        return CustomerCardDto.builder()
                .id(1L)
                .cartNumber("4102778822334896")
                .expDate("11/24")
                .cvv("543")
                .balance(BigDecimal.valueOf(5000))
                .currency(Currency.USD)
                .customerId(1L)
                .build();
    }

    public static CustomerCard getCustomerCardGutsBerserk() {
        return CustomerCard.builder()
                .id(1L)
                .cartNumber("4102778822334896")
                .expDate("11/24")
                .cvv("543")
                .balance(BigDecimal.valueOf(5000))
                .currency(Currency.USD)
                .customerId(1L)
                .build();
    }

    public static CustomerCard getCustomerCardGutsBerserkZeroBalance() {
        return CustomerCard.builder()
                .cartNumber("4102778822334896")
                .expDate("11/24")
                .cvv("543")
                .build();
    }

    public static CustomerCard getCustomerCardGutsBerserkTransient() {
        return CustomerCard.builder()
                .cartNumber("4102778822334896")
                .expDate("11/24")
                .cvv("543")
                .balance(BigDecimal.valueOf(5000))
                .currency(Currency.USD)
                .customerId(1L)
                .build();
    }

    public static CustomerCardDto getCustomerCardDtoJohnCena() {
        return CustomerCardDto.builder()
                .id(2L)
                .cartNumber("4102778822334888")
                .expDate("11/28")
                .cvv("540")
                .balance(BigDecimal.valueOf(4000))
                .currency(Currency.USD)
                .customerId(2L)
                .build();
    }

    public static CustomerCard getCustomerCardJohnCena() {
        return CustomerCard.builder()
                .id(2L)
                .cartNumber("4102778822334888")
                .expDate("11/28")
                .cvv("540")
                .balance(BigDecimal.valueOf(4000))
                .currency(Currency.USD)
                .customerId(2L)
                .build();
    }

    public static TransactionDto getTransactionDtoWithdrawal() {
        return TransactionDto.builder()
                .transactionType(TransactionType.TOP_UP)
                .paymentMethod(PaymentMethod.CARD)
                .amount(BigDecimal.valueOf(1000L))
                .currency(Currency.USD)
                .notificationUrl("test/http")
                .customer(getCustomerDtoGutsBerserk())
                .customerCard(getCustomerCardGutsBerserkDto())
                .build();
    }

    public static Transaction getTransactionWithdrawalInProgress() {
        return Transaction.builder()
                .id(UUID.randomUUID())
                .accountId(1L)
                .transactionType(TransactionType.TOP_UP)
                .createAt(LocalDateTime.now())
                .transactionStatus(TransactionStatus.IN_PROGRESS)
                .paymentMethod(PaymentMethod.CARD)
                .amount(BigDecimal.valueOf(1000L))
                .currency(Currency.USD)
                .notificationUrl("test/http")
                .customer(getCustomerGutsBerserk())
                .customerCard(getCustomerCardGutsBerserk())
                .build();
    }

    public static Merchant getMerchantForTest() {
        return Merchant.builder()
                .key("VGVzdA==")
                .merchantId("Test")
                .createAt(LocalDateTime.now())
                .build();
    }

    public static AccountMerchant getAccountForTest() {
        return AccountMerchant.builder()
                .createAt(LocalDateTime.now())
                .currency(Currency.USD)
                .balance(BigDecimal.valueOf(200000))
                .build();
    }

    public static Transaction getTransactionInProgressWithoutId() {
        return Transaction.builder()
                .transactionType(TransactionType.TOP_UP)
                .createAt(LocalDateTime.now())
                .transactionStatus(TransactionStatus.IN_PROGRESS)
                .paymentMethod(PaymentMethod.CARD)
                .amount(BigDecimal.valueOf(1000L))
                .currency(Currency.USD)
                .notificationUrl("test/http")
                .customer(getCustomerGutsBerserk())
                .customerCard(getCustomerCardGutsBerserk())
                .build();
    }
    public static Transaction getTransactionInProgress1() {
        return Transaction.builder()
                .id(UUID.randomUUID())
                .transactionType(TransactionType.WITHDRAWAL)
                .createAt(LocalDateTime.now())
                .transactionStatus(TransactionStatus.IN_PROGRESS)
                .paymentMethod(PaymentMethod.CARD)
                .amount(BigDecimal.valueOf(1000L))
                .currency(Currency.USD)
                .notificationUrl("test/http")
                .customer(getCustomerGutsBerserk())
                .customerCard(getCustomerCardGutsBerserk())
                .build();
    }

    public static Transaction getTransactionInProgress2() {
        return Transaction.builder()
                .id(UUID.randomUUID())
                .transactionType(TransactionType.TOP_UP)
                .createAt(LocalDateTime.now())
                .transactionStatus(TransactionStatus.IN_PROGRESS)
                .paymentMethod(PaymentMethod.CARD)
                .amount(BigDecimal.valueOf(1000L))
                .currency(Currency.USD)
                .notificationUrl("test/http")
                .customer(getCustomerGutsBerserk())
                .customerCard(getCustomerCardGutsBerserk())
                .build();
    }
    public static TransactionDto getTransactionDtoWithdrawal1() {
        return TransactionDto.builder()
                .transactionType(TransactionType.WITHDRAWAL)
                .paymentMethod(PaymentMethod.CARD)
                .amount(BigDecimal.valueOf(500L))
                .currency(Currency.USD)
                .notificationUrl("test/http")
                .customer(getCustomerDtoJohnCena())
                .customerCard(getCustomerCardDtoJohnCena())
                .build();
    }

    public static Transaction getTransactionTopUpInProgress() {
        return Transaction.builder()
                .id(UUID.randomUUID())
                .accountId(1L)
                .createAt(LocalDateTime.now())
                .transactionStatus(TransactionStatus.IN_PROGRESS)
                .transactionType(TransactionType.TOP_UP)
                .paymentMethod(PaymentMethod.CARD)
                .amount(BigDecimal.valueOf(200L))
                .currency(Currency.USD)
                .notificationUrl("test/http")
                .customer(getCustomerJohnCena())
                .customerCard(getCustomerCardJohnCena())
                .build();

    }

    public static Transaction getTransactionForPostRequest() {
        return Transaction.builder()
                .paymentMethod(PaymentMethod.CARD)
                .amount(BigDecimal.valueOf(200L))
                .currency(Currency.USD)
                .notificationUrl("test/http")
                .customer(getCustomerGutsBerserk())
                .customerCard(getCustomerCardGutsBerserk())
                .build();
    }

    public static Transaction getTransactionForFindAllList1() {
        return Transaction.builder()
                .accountId(1L)
                .createAt(LocalDateTime.now())
                .transactionType(TransactionType.TOP_UP)
                .paymentMethod(PaymentMethod.CARD)
                .amount(BigDecimal.valueOf(200L))
                .currency(Currency.USD)
                .notificationUrl("test/http1")
                .customer(getCustomerGutsBerserk())
                .customerCard(getCustomerCardGutsBerserk())
                .build();
    }
    public static Transaction getTransactionForFindAllList2() {
        return Transaction.builder()
                .accountId(1L)
                .createAt(LocalDateTime.now())
                .transactionType(TransactionType.TOP_UP)
                .paymentMethod(PaymentMethod.CARD)
                .amount(BigDecimal.valueOf(500L))
                .currency(Currency.USD)
                .notificationUrl("test/http2")
                .customer(getCustomerJohnCena())
                .customerCard(getCustomerCardJohnCena())
                .build();
    }
    public static Transaction getTransactionForFindAllList3() {
        return Transaction.builder()
                .accountId(1L)
                .createAt(LocalDateTime.now())
                .transactionType(TransactionType.TOP_UP)
                .paymentMethod(PaymentMethod.CARD)
                .amount(BigDecimal.valueOf(1200L))
                .currency(Currency.USD)
                .notificationUrl("test/http3")
                .customer(getCustomerGutsBerserk())
                .customerCard(getCustomerCardGutsBerserk())
                .build();
    }
    public static Transaction getTransactionForPostRequestZeroBalance() {
        return Transaction.builder()
                .paymentMethod(PaymentMethod.CARD)
                .amount(BigDecimal.valueOf(200L))
                .currency(Currency.USD)
                .notificationUrl("test/http")
                .customer(getCustomerGutsBerserkZeroBalance())
                .customerCard(getCustomerCardGutsBerserkZeroBalance())
                .build();

    }

    public static Transaction getTransactionTopUpSuccess() {
        return Transaction.builder()
                .id(UUID.randomUUID())
                .accountId(1L)
                .transactionStatus(TransactionStatus.SUCCESSFUL)
                .transactionType(TransactionType.TOP_UP)
                .paymentMethod(PaymentMethod.CARD)
                .amount(BigDecimal.valueOf(500L))
                .currency(Currency.USD)
                .notificationUrl("test/http")
                .customer(getCustomerJohnCena())
                .customerCard(getCustomerCardJohnCena())
                .build();
    }

    public static Transaction getTransactionTopUpFiled() {
        return Transaction.builder()
                .id(UUID.randomUUID())
                .accountId(1L)
                .transactionStatus(TransactionStatus.FAILED)
                .transactionType(TransactionType.TOP_UP)
                .paymentMethod(PaymentMethod.CARD)
                .amount(BigDecimal.valueOf(500L))
                .currency(Currency.USD)
                .notificationUrl("test/http")
                .customer(getCustomerJohnCena())
                .customerCard(getCustomerCardJohnCena())
                .build();
    }

    public static Transaction getTransactionPayoutInProgress() {
        return Transaction.builder()
                .id(UUID.randomUUID())
                .accountId(1L)
                .transactionStatus(TransactionStatus.IN_PROGRESS)
                .transactionType(TransactionType.TOP_UP)
                .paymentMethod(PaymentMethod.CARD)
                .amount(BigDecimal.valueOf(500L))
                .currency(Currency.USD)
                .notificationUrl("test/http")
                .customer(getCustomerJohnCena())
                .customerCard(getCustomerCardJohnCena())
                .build();
    }

    public static WebhookDto getWebhookDto() {
        return WebhookDto.builder().build();
    }

    public static Webhook getWebhook() {
        return Webhook.builder()
                .id(1L)
                .requestUrl("https://test.ru")
                .transactionId(UUID.randomUUID())
                .accountId(2L)
                .bodyRequest("Some Body")
                .transactionType(TransactionType.WITHDRAWAL)
                .build();
    }

    public static WebhookDto getWebhookDto1() {
        return WebhookDto.builder().build();
    }

    public static Webhook getWebhook1() {
        return Webhook.builder().build();
    }
}
