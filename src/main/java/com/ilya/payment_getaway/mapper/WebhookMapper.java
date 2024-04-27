package com.ilya.payment_getaway.mapper;

import com.ilya.payment_getaway.dto.WebhookDto;
import com.ilya.payment_getaway.entity.Webhook;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface WebhookMapper {
    WebhookDto map(Webhook webhook);

    @InheritInverseConfiguration
    Webhook map(WebhookDto webhookDto);
}
