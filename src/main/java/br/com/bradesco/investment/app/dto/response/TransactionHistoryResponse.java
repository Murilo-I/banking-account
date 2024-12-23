package br.com.bradesco.investment.app.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionHistoryResponse(String transactionType,
                                         String accountToHolderName,
                                         @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
                                         LocalDateTime transactionDate,
                                         BigDecimal amount) {
}
