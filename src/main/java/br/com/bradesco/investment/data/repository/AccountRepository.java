package br.com.bradesco.investment.data.repository;

import br.com.bradesco.investment.data.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByAccess_Email(String email);
}
