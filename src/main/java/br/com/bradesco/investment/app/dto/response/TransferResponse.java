package br.com.bradesco.investment.app.dto.response;

public record TransferResponse(String accountToHolderName, BalanceResponse accountFromBalance) {
}
