package com.ilya.payment_getaway.security;

import com.ilya.payment_getaway.entity.Merchant;
import com.ilya.payment_getaway.repository.MerchantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Base64;

@Service
@Slf4j
@RequiredArgsConstructor
public class SecurityService {
    private final MerchantRepository merchantRepository;

    public Mono<Merchant> checkMerchants(String merchantClimes) {
        String[] split = encode(merchantClimes);
        return Mono.from(merchantRepository.findAll()
                .filter(x -> x.getMerchantId().equals(split[0]) && decode(split[1], x.getKey()))
                .switchIfEmpty(Mono.error(()-> new SecurityException("Not auth user"))));
    }
    //Mono.error(()-> new NoAuthMerch(split[0])))
    private String[] encode(String merchantClimes) {
        return new String(Base64.getDecoder().decode(merchantClimes.substring(6))).split(":");
    }
    private boolean decode(String codeInput, String codeBase) {
        byte[] decode = Base64.getDecoder().decode(codeBase);
        String s = new String(decode);
        return codeInput.equals(s);
    }
}
