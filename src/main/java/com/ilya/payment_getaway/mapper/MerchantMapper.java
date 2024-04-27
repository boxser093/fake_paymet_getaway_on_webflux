package com.ilya.payment_getaway.mapper;

import com.ilya.payment_getaway.dto.MerchantDto;
import com.ilya.payment_getaway.dto.WebhookDto;
import com.ilya.payment_getaway.entity.Merchant;
import com.ilya.payment_getaway.entity.Webhook;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = AccountMapper.class, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface MerchantMapper {
    MerchantDto map(Merchant merchant);

    @InheritInverseConfiguration
    Merchant map(MerchantDto merchantDto);
}
