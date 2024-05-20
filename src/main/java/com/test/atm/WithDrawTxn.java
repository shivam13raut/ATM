package com.test.atm;

/**
 * DTO for Transaction Withdrawal, it holds the denomination and count of the denomination supposed to be withdrawn.
 *
 * @param denomination Actual denomination in multiples of 10 from ATM
 * @param denCount     Count of the denomination to be withdrawn
 */
public record WithDrawTxn(int denomination, int denCount) {
}
