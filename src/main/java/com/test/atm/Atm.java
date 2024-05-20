package com.test.atm;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * ATM class to handle the ATM operations
 */
@Slf4j
public class Atm {

    TreeMap<Integer, Integer> atmCashMap = new TreeMap<>();
    ReentrantLock lock = new ReentrantLock();

    public Atm() {
        atmCashMap.put(500, 20);
        atmCashMap.put(200, 30);
        atmCashMap.put(100, 20);
    }

    public Map<String, List<WithDrawTxn>> withdrawCash(int amount, String TxnId) {
        lock.lock();
        log.info("Starting withdrawal for: " + amount + " WithTxnId: " + TxnId);
        Map<String, List<WithDrawTxn>> withdrawnCash = new HashMap<>();
        try {
            // Validate withdrawal amount, if valid then proceed with withdrawal
            validateExpectedAmount(amount);
            // Get denominations in reverse order, since we need to consume higher ones first
            Set<Integer> atmDenominations = atmCashMap.descendingKeySet();
            List<WithDrawTxn> denomWithDrawList = new ArrayList<>();
            for (Integer currentDenomination : atmDenominations) {
                if (atmCashMap.get(currentDenomination) != 0 && amount != 0) {
                    int maxDenominationCount = amount / currentDenomination;
                    int allowedDenominationCount = Math.min(maxDenominationCount, atmCashMap.get(currentDenomination));
                    amount -= (allowedDenominationCount * currentDenomination);
                    denomWithDrawList.add(new WithDrawTxn(currentDenomination, allowedDenominationCount));
                }
                withdrawnCash.put(TxnId, denomWithDrawList);
            }
            // Throw exception if amount cannot be processed using available denominations
            if (amount != 0) {
                throw new RuntimeException("Amount cannot be disbursed using available denominations");
            }

            //Update atm Cash in Atm Map after withdrawal
            withdrawnCash.values().forEach(
                    withDrawTxns -> withDrawTxns.forEach(txn ->
                            atmCashMap.put(
                                    txn.denomination(),
                                    atmCashMap.get(txn.denomination()) - txn.denCount())
                    ));
        } finally {
            lock.unlock();
        }
        return withdrawnCash;
    }

    private void validateExpectedAmount(int amount) {
        if (amount == 0) {
            throw new RuntimeException("Amount should be greater than 0");
        }
        if (amount % 10 != 0) {
            throw new RuntimeException("Amount should be multiple of 10");
        }
        int balance = atmCashMap.entrySet().stream()
                .mapToInt(den -> den.getKey() * den.getValue())
                .sum();
        if (balance == 0) {
            throw new RuntimeException("There is no cash in ATM");
        }
        if (balance < amount) {
            throw new RuntimeException("Atm balance is low");
        }
    }

    public long getAtmBalance() {
        return atmCashMap.entrySet().stream()
                .mapToLong(den -> (long) den.getKey() * den.getValue())
                .sum();
    }

    public Map<Integer, Integer> getAtmCashMap() {
        return atmCashMap;
    }
}
