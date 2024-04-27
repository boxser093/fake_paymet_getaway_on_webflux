package com.ilya.payment_getaway.service.impl;

import com.ilya.payment_getaway.entity.Customer;
import com.ilya.payment_getaway.entity.CustomerCard;
import com.ilya.payment_getaway.repository.CustomerCardRepository;
import com.ilya.payment_getaway.repository.CustomerRepository;
import com.ilya.payment_getaway.util.DateUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private CustomerCardRepository customerCardRepository;
    @InjectMocks
    private CustomerServiceImpl customerService;

    @Test
    void findCustomerByFirstNameAndLastName() {
        // given
        Customer customerJohnCena = DateUtils.getCustomerJohnCena();
        String firstName = customerJohnCena.getFirstName();
        String lastName = customerJohnCena.getLastName();
        // when
        when(customerRepository.findCustomerByFirstNameAndLastName(firstName, lastName))
                .thenReturn(Mono.just(customerJohnCena));
        // then
        StepVerifier
                .create(customerService.findCustomerByFirstNameAndLastName("John", "Cena"))
                .expectNextMatches(customer -> customer.getId().equals(customerJohnCena.getId())
                        && customer.getFirstName().equals(customerJohnCena.getFirstName())
                        && customer.getLastName().equals(customerJohnCena.getLastName()))
                .expectComplete()
                .verify();
    }

    @Test
    void givenFindById_whenCustomerRegistered_thenSuccessfulness() {
        // given
        CustomerCard customerCardGutsBerserk = DateUtils.getCustomerCardGutsBerserk();
        Customer given = DateUtils.getCustomerGutsBerserk();
        // when
        when(customerCardRepository.findAll())
                .thenReturn(Flux.just(customerCardGutsBerserk));
        when(customerRepository.findById(1L))
                .thenReturn(Mono.just(given));

        // then
        StepVerifier
                .create(customerService.findById(1L))
                .expectNextMatches(customer -> customer.getId().equals(given.getId())
                        && customer.getFirstName().equals(given.getFirstName())
                        && customer.getLastName().equals(given.getLastName()))
                .expectComplete()
                .verify();
    }

    @Test
    void create() {
        //given
        Customer build = Customer.builder()
                .country("USA")
                .firstName("John")
                .lastName("Cena")
                .createAt(any())
                .build();
        Customer customerJohnCena = DateUtils.getCustomerJohnCena();
        //when
        when(customerRepository.save(build)).thenReturn(Mono.just(customerJohnCena.toBuilder()
                .createAt(any())
                .build()));
        //then
        StepVerifier
                .create(customerService.create(build))
                .expectNextMatches(customer -> customer.getId().equals(customerJohnCena.getId())
                        && customer.getFirstName().equals(customerJohnCena.getFirstName())
                        && customer.getLastName().equals(customerJohnCena.getLastName()))
                .expectComplete()
                .verify();
    }

    @Test
    void update() {
        //given
        Customer beforeUpdate = DateUtils.getCustomerGutsBerserk().toBuilder()
                .createAt(LocalDateTime.now())
                .updateAt(LocalDateTime.now())
                .build();

        Customer afterUpdate = Customer.builder()
                .id(beforeUpdate.getId())
                .firstName("Griffith")
                .lastName("White Falcon")
                .updateAt(LocalDateTime.now())
                .build();
        //when
        when(customerRepository.findById(beforeUpdate.getId())).thenReturn(Mono.just(beforeUpdate));
        when(customerRepository.save(any(Customer.class))).thenReturn(Mono.just(afterUpdate));
        //then
        StepVerifier
                .create(customerService.update(beforeUpdate))
                .expectNextMatches(user -> user.getId().equals(beforeUpdate.getId())
                        && user.getFirstName().equals(afterUpdate.getFirstName())
                        && user.getLastName().equals(afterUpdate.getLastName()))
                .expectComplete()
                .verify();
    }

    @Test
    void findAll() {
        //given
        Customer customerJohnCena = DateUtils.getCustomerJohnCena();
        Customer customerGutsBerserk = DateUtils.getCustomerGutsBerserk();
        Flux<Customer> just = Flux.just(customerJohnCena, customerGutsBerserk);
        //when
        when(customerRepository.findAll()).thenReturn(just);
        //then
        StepVerifier
                .create(customerService.findAll())
                .expectNext(customerJohnCena)
                .expectNext(customerGutsBerserk)
                .verifyComplete();
    }
}