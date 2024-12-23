package br.com.bradesco.investment.config.auth;

import br.com.bradesco.investment.app.dto.request.AccessRequest;
import br.com.bradesco.investment.app.dto.response.TokenResponse;
import br.com.bradesco.investment.app.usecase.AuthenticationUseCase;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService implements AuthenticationUseCase {

    AuthenticationManager authManager;
    TokenService tokenService;

    public AuthenticationService(AuthenticationManager authManager, TokenService tokenService) {
        this.authManager = authManager;
        this.tokenService = tokenService;
    }

    @Override
    public TokenResponse authenticate(AccessRequest access) {
        var usernamePasswordToken =
                new UsernamePasswordAuthenticationToken(access.getEmail(), access.getPassword());

        return tokenService.generateToken(authManager.authenticate(usernamePasswordToken));
    }
}
