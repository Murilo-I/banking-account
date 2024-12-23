package br.com.bradesco.investment.app.usecase;

import br.com.bradesco.investment.app.dto.request.AccountRequest;
import br.com.bradesco.investment.app.dto.response.*;
import br.com.bradesco.investment.app.dto.request.DepositRequest;
import br.com.bradesco.investment.app.dto.request.TransferRequest;

public interface AccountUseCase {

    AccountResponse openAccount(AccountRequest holderName);

    BalanceResponse deposit(DepositRequest request, Long originAccountNumber);

    TransferResponse transfer(TransferRequest request, Long originAccountNumber);

    BankStatementResponse generateStatement(Long accountNumber);

}
