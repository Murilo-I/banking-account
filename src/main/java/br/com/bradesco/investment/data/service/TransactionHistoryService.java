package br.com.bradesco.investment.data.service;

import br.com.bradesco.investment.app.dto.response.BankStatementResponse;
import br.com.bradesco.investment.app.dto.response.TransactionHistoryResponse;
import br.com.bradesco.investment.app.usecase.TransactionHistoryUseCase;
import br.com.bradesco.investment.data.repository.AccountRepository;
import br.com.bradesco.investment.data.repository.TransactionHistoryRepository;
import br.com.bradesco.investment.util.exception.AccountNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class TransactionHistoryService implements TransactionHistoryUseCase {

    final TransactionHistoryRepository transactionRepository;
    final AccountRepository accountRepository;

    public TransactionHistoryService(TransactionHistoryRepository transactionRepository,
                                     AccountRepository accountRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }

    @Override
    public BankStatementResponse getBankAccountHistory(Long accountNumber) {
        var account = accountRepository.findById(accountNumber)
                .orElseThrow(AccountNotFoundException::new);
        var transactionHistory = transactionRepository.findByHistoryIdAccountNumber(accountNumber)
                .stream().map(statement -> new TransactionHistoryResponse(
                        statement.getTransactionType().name(),
                        statement.getAccountToHolderName(),
                        statement.getHistoryId().getTransactionDate(),
                        statement.getAmount()
                ))
                .toList();

        return new BankStatementResponse(account.getBalance(), transactionHistory);
    }
}
