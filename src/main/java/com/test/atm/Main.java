package com.test.atm;

import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

/**
 * Driver for te ATM implementation for testing ATM implementation
 */

@Slf4j
public class Main {
    public static void main(String[] args) { // In application, such exceptions can be handled by Global handlers and makes our operations transactional
        //Initialize ATM with cash
        Atm atm = new Atm();
        AtmOperation atmOperation = new AtmOperation(atm);
        atmOperation.withDrawCash(5200, UUID.randomUUID().toString());
        log.info("ATM Balance: " + atm.getAtmBalance());
    }
}