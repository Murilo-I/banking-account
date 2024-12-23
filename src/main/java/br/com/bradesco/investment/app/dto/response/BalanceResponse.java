package br.com.bradesco.investment.app.dto.response;

import java.math.BigDecimal;

public record BalanceResponse(BigDecimal previousBalance, BigDecimal currentBalance) {
}
