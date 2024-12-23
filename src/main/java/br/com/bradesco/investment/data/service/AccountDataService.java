package br.com.bradesco.investment.data.service;

import br.com.bradesco.investment.app.dto.response.AccountResponse;
import br.com.bradesco.investment.app.dto.response.BalanceResponse;
import br.com.bradesco.investment.app.dto.response.TransferResponse;
import br.com.bradesco.investment.app.usecase.AccountDataUseCase;
import br.com.bradesco.investment.data.model.Account;
import br.com.bradesco.investment.data.model.AccountAccess;
import br.com.bradesco.investment.data.model.TransactionHistory;
import br.com.bradesco.investment.data.repository.AccountAccessRepository;
import br.com.bradesco.investment.data.repository.AccountRepository;
import br.com.bradesco.investment.data.repository.TransactionHistoryRepository;
import br.com.bradesco.investment.util.exception.AccountNotFoundException;
import br.com.bradesco.investment.util.exception.InsufficientBalanceException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Optional;

import static br.com.bradesco.investment.data.model.AccountType.BASIC_ACCOUNT;
import static br.com.bradesco.investment.data.model.TransactionType.DEPOSIT;
import static br.com.bradesco.investment.data.model.TransactionType.TRANSFER;

@Service
public class AccountDataService implements AccountDataUseCase {

    final AccountRepository accountRepository;
    final AccountAccessRepository accessRepository;
    final TransactionHistoryRepository transactionRepository;

    public AccountDataService(AccountRepository accountRepository,
                              AccountAccessRepository accessRepository,
                              TransactionHistoryRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.accessRepository = accessRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    @Transactional
    public AccountResponse create(String holderName, String email, String password) {
        var createdAccess = accessRepository.save(
                AccountAccess.builder()
                        .email(email)
                        .password(new BCryptPasswordEncoder().encode(password))
                        .build()
        );
        var createdAccount = accountRepository.save(
                Account.builder()
                        .holderName(holderName)
                        .balance(new BigDecimal(BigInteger.ZERO))
                        .access(createdAccess)
                        .accountType(BASIC_ACCOUNT)
                        .build()
        );

        return new AccountResponse(createdAccount.getHolderName(), createdAccount.getBalance());
    }

    @Override
    @Transactional
    public BalanceResponse deposit(Long accountNumber, BigDecimal amount) {
        var account = findByAccountNumber(accountNumber)
                .orElseThrow(AccountNotFoundException::new);
        var prevBalance = account.getBalance();
        account.setBalance(prevBalance.add(amount));

        transactionRepository.save(TransactionHistory.builder()
                .historyId(TransactionHistory.TransactionHistoryId.builder()
                        .accountNumber(account.getAccountNumber())
                        .transactionDate(LocalDateTime.now())
                        .build())
                .transactionType(DEPOSIT)
                .amount(amount)
                .build());

        return new BalanceResponse(prevBalance, account.getBalance());
    }

    @Override
    @Transactional
    public TransferResponse transfer(Long accountTo, Long accountFrom, BigDecimal amount) {
        var account1 = findByAccountNumber(accountFrom)
                .orElseThrow(AccountNotFoundException::new);
        var account2 = findByAccountNumber(accountTo)
                .orElseThrow(AccountNotFoundException::new);

        var prevBalance = account1.getBalance();

        if (prevBalance.compareTo(amount) < 0)
            throw new InsufficientBalanceException();

        account1.setBalance(prevBalance.subtract(amount));
        account2.setBalance(account2.getBalance().add(amount));

        transactionRepository.save(TransactionHistory.builder()
                .historyId(TransactionHistory.TransactionHistoryId.builder()
                        .accountNumber(account1.getAccountNumber())
                        .transactionDate(LocalDateTime.now())
                        .build())
                .accountToHolderName(account2.getHolderName())
                .transactionType(TRANSFER)
                .amount(amount)
                .build());

        return new TransferResponse(account2.getHolderName(),
                new BalanceResponse(prevBalance, account1.getBalance()));
    }

    @Override
    public Optional<Account> findByAccountNumber(Long accountNumber) {
        return accountRepository.findById(accountNumber);
    }

    @Override
    public Optional<Account> findByEmail(String email) {
        return accountRepository.findByAccess_Email(email);
    }
}
