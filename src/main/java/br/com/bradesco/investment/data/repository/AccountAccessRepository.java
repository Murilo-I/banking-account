package br.com.bradesco.investment.data.repository;

import br.com.bradesco.investment.data.model.AccountAccess;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountAccessRepository extends JpaRepository<AccountAccess, Long> {

    Optional<AccountAccess> findByEmail(String email);
}
