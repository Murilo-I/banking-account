package br.com.bradesco.investment.app.service;

import br.com.bradesco.investment.app.dto.request.AccountRequest;
import br.com.bradesco.investment.app.dto.request.DepositRequest;
import br.com.bradesco.investment.app.dto.request.TransferRequest;
import br.com.bradesco.investment.app.dto.response.AccountResponse;
import br.com.bradesco.investment.app.dto.response.BalanceResponse;
import br.com.bradesco.investment.app.dto.response.BankStatementResponse;
import br.com.bradesco.investment.app.dto.response.TransferResponse;
import br.com.bradesco.investment.app.usecase.AccountDataUseCase;
import br.com.bradesco.investment.app.usecase.AccountUseCase;
import br.com.bradesco.investment.app.usecase.TransactionHistoryUseCase;
import org.springframework.stereotype.Service;

@Service
public class AccountService implements AccountUseCase {

    final AccountDataUseCase accountUseCase;
    final TransactionHistoryUseCase transactionUseCase;

    public AccountService(AccountDataUseCase accountUseCase,
                          TransactionHistoryUseCase transactionUseCase) {
        this.accountUseCase = accountUseCase;
        this.transactionUseCase = transactionUseCase;
    }

    @Override
    public AccountResponse openAccount(AccountRequest request) {
        return accountUseCase.create(request.getHolderName(), request.getEmail(), request.getPassword());
    }

    @Override
    public BalanceResponse deposit(DepositRequest request, Long originAccountNumber) {
        return accountUseCase.deposit(originAccountNumber, request.getAmount());
    }

    @Override
    public TransferResponse transfer(TransferRequest request, Long originAccountNumber) {
        return accountUseCase.transfer(request.getAccountTo(), originAccountNumber,
                request.getAmount());
    }

    @Override
    public BankStatementResponse generateStatement(Long accountNumber) {
        return transactionUseCase.getBankAccountHistory(accountNumber);
    }
}
