package br.com.bradesco.investment.app.usecase;

import br.com.bradesco.investment.app.dto.request.AccessRequest;
import br.com.bradesco.investment.app.dto.response.TokenResponse;

public interface AuthenticationUseCase {

    TokenResponse authenticate(AccessRequest access);
}
