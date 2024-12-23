package br.com.bradesco.investment.data.service;

import br.com.bradesco.investment.data.model.Account;
import br.com.bradesco.investment.data.model.TransactionHistory;
import br.com.bradesco.investment.data.repository.AccountRepository;
import br.com.bradesco.investment.data.repository.TransactionHistoryRepository;
import br.com.bradesco.investment.util.exception.AccountNotFoundException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static br.com.bradesco.investment.data.model.TransactionType.TRANSFER;
import static br.com.bradesco.investment.data.service.AccountDataServiceTest.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TransactionHistoryServiceTest {

    TransactionHistoryRepository transactionRepository = mock(TransactionHistoryRepository.class);
    AccountRepository accountRepository = mock(AccountRepository.class);
    TransactionHistoryService service = new TransactionHistoryService(transactionRepository,
            accountRepository);

    @Test
    void getBankAccountHistorySuccess() {
        when(accountRepository.findById(anyLong()))
                .thenReturn(Optional.of(Account.builder()
                        .balance(BigDecimal.TEN)
                        .build())
                );
        when(transactionRepository.findByHistoryIdAccountNumber(anyLong()))
                .thenReturn(List.of(TransactionHistory.builder()
                        .transactionType(TRANSFER)
                        .accountToHolderName(HOLDOR)
                        .historyId(TransactionHistory.TransactionHistoryId.builder()
                                .accountNumber(ACCOUNT_TO)
                                .transactionDate(LocalDateTime.now())
                                .build())
                        .amount(BigDecimal.TEN)
                        .build())
                );

        var response = service.getBankAccountHistory(ACCOUNT_TO);

        assertFalse(response.transactionHistory().isEmpty());
        assertEquals(BigDecimal.TEN, response.currentBalance());
        assertEquals(TRANSFER.name(), response.transactionHistory().get(0).transactionType());
        assertEquals(HOLDOR, response.transactionHistory().get(0).accountToHolderName());
    }

    @Test
    void throwAccountNotFoundTest() {
        when(accountRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class,
                () -> service.getBankAccountHistory(ORIGIN_ACCOUNT));
    }
}
