package com.ilya.payment_getaway.mapper;

import com.ilya.payment_getaway.dto.CustomerCardDto;
import com.ilya.payment_getaway.dto.TransactionDto;
import com.ilya.payment_getaway.entity.Transaction;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {CustomerCardMapper.class, CustomerMapper.class}, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface TransactionMapper {

    TransactionDto map(Transaction transaction);

    @InheritInverseConfiguration
    Transaction map(TransactionDto transactionDto);
}
