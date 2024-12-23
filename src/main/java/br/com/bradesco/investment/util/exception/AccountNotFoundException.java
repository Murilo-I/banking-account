package br.com.bradesco.investment.util.exception;

public class AccountNotFoundException extends RuntimeException {

    public AccountNotFoundException() {
        super("Invalid account Number.");
    }
}
