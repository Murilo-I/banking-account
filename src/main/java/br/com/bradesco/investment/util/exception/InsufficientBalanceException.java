package br.com.bradesco.investment.util.exception;

public class InsufficientBalanceException extends RuntimeException {

    public InsufficientBalanceException() {
        super("Insufficient Balance.");
    }
}
