package br.com.bradesco.investment.app.usecase;

import br.com.bradesco.investment.app.dto.response.BankStatementResponse;

public interface TransactionHistoryUseCase {

    BankStatementResponse getBankAccountHistory(Long accountNumber);
}
