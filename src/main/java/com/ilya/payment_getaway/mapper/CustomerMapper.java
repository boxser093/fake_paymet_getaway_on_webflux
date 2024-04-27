package com.ilya.payment_getaway.mapper;


import com.ilya.payment_getaway.dto.CustomerDto;
import com.ilya.payment_getaway.entity.Customer;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = CustomerCardMapper.class, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface CustomerMapper {
    CustomerDto map(Customer customer);

    @InheritInverseConfiguration
    Customer map(CustomerDto customer);
}
