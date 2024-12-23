package br.com.bradesco.investment.config.auth;

import br.com.bradesco.investment.app.usecase.AccountDataUseCase;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthDetailsService implements UserDetailsService {

    final AccountDataUseCase useCase;

    public AuthDetailsService(AccountDataUseCase useCase) {
        this.useCase = useCase;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var account = useCase.findByEmail(username);

        if (account.isPresent()) return account.get();

        throw new UsernameNotFoundException("Invalid e-mail or password.");
    }
}
