package com.ilya.payment_getaway.mapper;

import com.ilya.payment_getaway.dto.AccountDto;
import com.ilya.payment_getaway.entity.AccountMerchant;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AccountMapper {
    AccountDto map(AccountMerchant accountMerchant);
    @InheritInverseConfiguration
    AccountMerchant map(AccountDto accountDto);

    List<AccountDto> map(List<AccountMerchant> accountMerchants);
}
