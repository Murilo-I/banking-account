package br.com.bradesco.investment.app.controller;

import br.com.bradesco.investment.app.dto.request.AccessRequest;
import br.com.bradesco.investment.app.dto.response.TokenResponse;
import br.com.bradesco.investment.app.usecase.AuthenticationUseCase;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    final AuthenticationUseCase useCase;

    public AuthenticationController(AuthenticationUseCase useCase) {
        this.useCase = useCase;
    }

    @PostMapping
    ResponseEntity<TokenResponse> authenticate(@Valid @RequestBody AccessRequest access) {
        return ResponseEntity.ok().body(useCase.authenticate(access));
    }
}
