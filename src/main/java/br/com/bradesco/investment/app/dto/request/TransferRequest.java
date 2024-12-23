package br.com.bradesco.investment.app.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class TransferRequest {
    @NotNull(message = "Account number to transfer cannot be null.")
    Long accountTo;
    @NotNull(message = "Amount cannot be null.")
    @Positive(message = "Amount to transfer must be greater than 0.")
    BigDecimal amount;
}
