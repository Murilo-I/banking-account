package br.com.bradesco.investment.app.dto.response;

import java.math.BigDecimal;

public record AccountResponse(String holderName, BigDecimal balance) {
}
