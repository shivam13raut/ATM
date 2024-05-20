package com.test.atm;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

/**
 * Proxy over the base ATM implementation, works as a facade for the ATM operations and thus allows
 * decoupling between the client and the ATM.
 */
@Slf4j
public class AtmOperation {

    /**
     * Injectable ATM implementation, allows easy swapping of the ATM implementation
     */
    private final Atm atm;

    /**
     * Inject relevant ATM implementation
     *
     * @param atm ATM implementation
     */
    public AtmOperation(Atm atm) {
        this.atm = atm;
    }

    /**
     * Withdraw cash from the ATM
     * @param amount Amount to be withdrawn
     * @param txnId  Unique transaction id
     */
    public void withDrawCash(int amount, String txnId) {
        log.info("TxnId: " + txnId + " Amount: " + amount);
        Map<String, List<WithDrawTxn>> withDrawnCash = atm.withdrawCash(amount, txnId);
        for (Map.Entry<String, List<WithDrawTxn>> cashWithdraw : withDrawnCash.entrySet()) {
            log.info("TxnId: " + cashWithdraw.getKey() + " countWithdrawn: " + cashWithdraw.getValue());
        }
    }
}
