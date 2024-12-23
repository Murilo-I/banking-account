package br.com.bradesco.investment.data.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TransactionHistory {

    @EmbeddedId
    TransactionHistoryId historyId;

    String accountToHolderName;

    @Column(nullable = false)
    BigDecimal amount;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    TransactionType transactionType;

    @Getter
    @Builder
    @ToString
    @Embeddable
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TransactionHistoryId {
        @Column(nullable = false)
        Long accountNumber;
        @Column(nullable = false)
        LocalDateTime transactionDate;
    }
}
