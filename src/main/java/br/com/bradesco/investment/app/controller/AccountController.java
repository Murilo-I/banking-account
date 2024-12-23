package br.com.bradesco.investment.app.controller;

import br.com.bradesco.investment.app.dto.request.AccountRequest;
import br.com.bradesco.investment.app.dto.request.DepositRequest;
import br.com.bradesco.investment.app.dto.request.TransferRequest;
import br.com.bradesco.investment.app.dto.response.AccountResponse;
import br.com.bradesco.investment.app.dto.response.BalanceResponse;
import br.com.bradesco.investment.app.dto.response.BankStatementResponse;
import br.com.bradesco.investment.app.dto.response.TransferResponse;
import br.com.bradesco.investment.app.usecase.AccountUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/account")
public class AccountController {

    final AccountUseCase useCase;

    public AccountController(AccountUseCase useCase) {
        this.useCase = useCase;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<AccountResponse> create(@Valid @RequestBody AccountRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(useCase.openAccount(request));
    }

    @PostMapping(value = "/deposit", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<BalanceResponse> deposit(@Valid @RequestBody DepositRequest request,
                                            @RequestParam Long originAccountNumber) {
        return ResponseEntity.ok(useCase.deposit(request, originAccountNumber));
    }

    @PostMapping(value = "/transfer", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<TransferResponse> transfer(@Valid @RequestBody TransferRequest request,
                                              @RequestParam Long originAccountNumber) {
        return ResponseEntity.ok(useCase.transfer(request, originAccountNumber));
    }

    @GetMapping("/{id}")
    ResponseEntity<BankStatementResponse> generateBankStatement(@PathVariable Long id) {
        return ResponseEntity.ok(useCase.generateStatement(id));
    }
}
