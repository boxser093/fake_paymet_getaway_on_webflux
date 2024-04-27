package com.ilya.payment_getaway.mapper;

import com.ilya.payment_getaway.dto.CustomerCardDto;
import com.ilya.payment_getaway.entity.CustomerCard;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = CustomerMapper.class, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface CustomerCardMapper {
    CustomerCardDto map(CustomerCard customerCard);

    @InheritInverseConfiguration
    CustomerCard map(CustomerCardDto customerCard);
}
