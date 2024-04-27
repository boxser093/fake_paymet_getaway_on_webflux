package com.ilya.payment_getaway.service;

import com.ilya.payment_getaway.entity.AccountMerchant;
import com.ilya.payment_getaway.entity.CustomerCard;
import com.ilya.payment_getaway.entity.Transaction;

public interface CheckBalanceInterface {
    boolean checkBalanceForTopUp(Transaction transaction, CustomerCard customerCard);
    boolean checkBalanceForPayout(Transaction transaction, AccountMerchant accountMerchant);
}
