package br.com.bradesco.investment.app.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record BankStatementResponse(BigDecimal currentBalance,
                                    List<TransactionHistoryResponse> transactionHistory) {
}
