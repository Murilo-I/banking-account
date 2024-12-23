package br.com.bradesco.investment.data.repository;

import br.com.bradesco.investment.data.model.TransactionHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionHistoryRepository extends JpaRepository<TransactionHistory, TransactionHistory.TransactionHistoryId> {

    List<TransactionHistory> findByHistoryIdAccountNumber(Long accountNumber);
}
