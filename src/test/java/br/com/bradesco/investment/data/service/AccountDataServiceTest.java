package br.com.bradesco.investment.data.service;

import br.com.bradesco.investment.data.model.Account;
import br.com.bradesco.investment.data.model.AccountAccess;
import br.com.bradesco.investment.data.model.TransactionHistory;
import br.com.bradesco.investment.data.repository.AccountAccessRepository;
import br.com.bradesco.investment.data.repository.AccountRepository;
import br.com.bradesco.investment.data.repository.TransactionHistoryRepository;
import br.com.bradesco.investment.util.exception.AccountNotFoundException;
import br.com.bradesco.investment.util.exception.InsufficientBalanceException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AccountDataServiceTest {

    static final String HOLDOR = "Holdor";
    static final long ORIGIN_ACCOUNT = 1L;
    static final long ACCOUNT_TO = 2L;
    static final String EMAIL = "email@email.com";

    AccountRepository accountRepository = mock(AccountRepository.class);
    AccountAccessRepository accessRepository = mock(AccountAccessRepository.class);
    TransactionHistoryRepository transactionRepository = mock(TransactionHistoryRepository.class);
    AccountDataService service = new AccountDataService(accountRepository, accessRepository,
            transactionRepository);


    @Test
    void createAccountSuccess() {
        when(accessRepository.save(any(AccountAccess.class)))
                .thenReturn(AccountAccess.builder()
                        .email(EMAIL)
                        .password("strong")
                        .build()
                );
        when(accountRepository.save(any(Account.class)))
                .thenReturn(Account.builder()
                        .balance(BigDecimal.ZERO)
                        .holderName(HOLDOR)
                        .build()
                );

        var response = service.create(HOLDOR, EMAIL, "strong");

        assertEquals(HOLDOR, response.holderName());
        assertEquals(BigDecimal.ZERO, response.balance());
    }

    @Test
    void depositSuccess() {
        when(accountRepository.findById(anyLong()))
                .thenReturn(Optional.of(Account.builder()
                        .balance(BigDecimal.TEN)
                        .build())
                );
        when(transactionRepository.save(any(TransactionHistory.class)))
                .thenReturn(TransactionHistory.builder().build());

        var response = service.deposit(ORIGIN_ACCOUNT, BigDecimal.TEN);

        assertEquals(BigDecimal.TEN, response.previousBalance());
        assertEquals(BigDecimal.valueOf(20), response.currentBalance());
    }

    @Test
    void depositError() {
        when(accountRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class,
                () -> service.deposit(ORIGIN_ACCOUNT, BigDecimal.TEN));
    }

    @Test
    void transferSuccess() {
        when(accountRepository.findById(ORIGIN_ACCOUNT))
                .thenReturn(Optional.of(Account.builder()
                        .balance(BigDecimal.TEN)
                        .build())
                );
        when(accountRepository.findById(ACCOUNT_TO))
                .thenReturn(Optional.of(Account.builder()
                        .balance(BigDecimal.ZERO)
                        .holderName(HOLDOR)
                        .build())
                );
        when(transactionRepository.save(any(TransactionHistory.class)))
                .thenReturn(TransactionHistory.builder().build());

        var response = service.transfer(ACCOUNT_TO, ORIGIN_ACCOUNT, BigDecimal.ONE);

        assertEquals(HOLDOR, response.accountToHolderName());
        assertEquals(BigDecimal.TEN, response.accountFromBalance().previousBalance());
        assertEquals(BigDecimal.valueOf(9), response.accountFromBalance().currentBalance());
    }

    @Test
    void transferErrorOriginAccountNotFound() {
        when(accountRepository.findById(ORIGIN_ACCOUNT)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class,
                () -> service.transfer(ACCOUNT_TO, ORIGIN_ACCOUNT, BigDecimal.TEN));
    }

    @Test
    void transferErrorAccountToNotFound() {
        when(accountRepository.findById(ORIGIN_ACCOUNT))
                .thenReturn(Optional.of(Account.builder().build()));
        when(accountRepository.findById(ACCOUNT_TO)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class,
                () -> service.transfer(ACCOUNT_TO, ORIGIN_ACCOUNT, BigDecimal.TEN));
    }

    @Test
    void transferErrorInsufficientBalance() {
        when(accountRepository.findById(anyLong()))
                .thenReturn(Optional.of(Account.builder()
                        .balance(BigDecimal.ONE)
                        .build())
                );

        assertThrows(InsufficientBalanceException.class,
                () -> service.transfer(ACCOUNT_TO, ORIGIN_ACCOUNT, BigDecimal.TEN));
    }

    @Test
    void findAccountByEmailTest() {
        when(accountRepository.findByAccess_Email(EMAIL))
                .thenReturn(Optional.of(Account.builder()
                        .access(AccountAccess.builder()
                                .email(EMAIL)
                                .build())
                        .build())
                );

        var account = service.findByEmail(EMAIL);

        assertTrue(account.isPresent());
        assertEquals(EMAIL, account.get().getAccess().getEmail());
    }
}
