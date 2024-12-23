package br.com.bradesco.investment.app.usecase;

import br.com.bradesco.investment.app.dto.response.AccountResponse;
import br.com.bradesco.investment.app.dto.response.BalanceResponse;
import br.com.bradesco.investment.app.dto.response.TransferResponse;
import br.com.bradesco.investment.data.model.Account;

import java.math.BigDecimal;
import java.util.Optional;

public interface AccountDataUseCase {

    AccountResponse create(String holderName, String email, String password);

    BalanceResponse deposit(Long accountNumber, BigDecimal amount);

    TransferResponse transfer(Long accountTo, Long accountFrom, BigDecimal amount);

    Optional<Account> findByAccountNumber(Long accountNumber);

    Optional<Account> findByEmail(String email);
}
