package br.com.bradesco.investment.app.controller;

import br.com.bradesco.investment.app.dto.response.*;
import br.com.bradesco.investment.app.usecase.AccountDataUseCase;
import br.com.bradesco.investment.app.usecase.TransactionHistoryUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static br.com.bradesco.investment.app.controller.AuthenticationControllerTest.EMAIL;
import static br.com.bradesco.investment.app.controller.AuthenticationControllerTest.STRONG;
import static br.com.bradesco.investment.data.model.TransactionType.DEPOSIT;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AccountControllerTest {

    static final String BASE_URL = "/account";
    public static final String HOLDOR = "Holdor";

    @Autowired
    MockMvc mvc;

    @MockitoBean
    AccountDataUseCase accountUseCase;

    @MockitoBean
    TransactionHistoryUseCase transactionUseCase;

    @Test
    void createAccountSuccess() throws Exception {
        when(accountUseCase.create(anyString(), anyString(), anyString()))
                .thenReturn(new AccountResponse(HOLDOR, BigDecimal.ZERO));

        var payload = """
                {
                    "holderName": "Murilo Tegani",
                    "email": "%s",
                    "password": "%s.123"
                }
                """.formatted(EMAIL, STRONG);

        mvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(payload)
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.balance", is(0)));
    }

    @Test
    void createAccountError() throws Exception {
        var payload = """
                {
                    "email": "murilo.tegani",
                    "password": "%s"
                }
                """.formatted(STRONG);

        mvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(payload)
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.holderName",
                        is("Holder name cannot be blank."))
                )
                .andExpect(jsonPath("$.email",
                        is("Invalid e-mail format."))
                )
                .andExpect(jsonPath("$.password",
                        is("Password length must be between 8 and 25."))
                );
    }

    @Test
    @WithMockUser("test-account")
    void depositSuccess() throws Exception {
        when(accountUseCase.deposit(anyLong(), any(BigDecimal.class)))
                .thenReturn(new BalanceResponse(BigDecimal.ZERO, BigDecimal.ONE));

        var payload = """
                {
                    "amount": 10000
                }
                """;

        mvc.perform(post(BASE_URL + "/deposit")
                        .queryParam("originAccountNumber", "1983")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(payload)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.previousBalance", is(0)))
                .andExpect(jsonPath("$.currentBalance", is(1)));
    }

    @Test
    @WithMockUser("test-account")
    void depositError() throws Exception {
        var payload = """
                {
                    "amount": -1
                }
                """;

        mvc.perform(post(BASE_URL + "/deposit")
                        .queryParam("originAccountNumber", "1984")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(payload)
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.amount",
                        is("Amount to deposit must be greater than 0."))
                );
    }

    @Test
    @WithMockUser("test-account")
    void transferSuccess() throws Exception {
        when(accountUseCase.transfer(anyLong(), anyLong(), any(BigDecimal.class)))
                .thenReturn(new TransferResponse(HOLDOR,
                        new BalanceResponse(BigDecimal.ZERO, BigDecimal.ONE)));

        var payload = """
                {
                    "accountTo": 2000,
                    "amount": 20000
                }
                """;

        mvc.perform(post(BASE_URL + "/transfer")
                        .queryParam("originAccountNumber", "1985")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(payload)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accountFromBalance.previousBalance",
                        is(0)))
                .andExpect(jsonPath("$.accountFromBalance.currentBalance",
                        is(1)))
                .andExpect(jsonPath("$.accountToHolderName", is(HOLDOR)));
    }

    @Test
    @WithMockUser("test-account")
    void transferError() throws Exception {
        mvc.perform(post(BASE_URL + "/transfer")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser("test-account")
    void generateStatementSuccess() throws Exception {
        when(transactionUseCase.getBankAccountHistory(anyLong()))
                .thenReturn(new BankStatementResponse(BigDecimal.valueOf(100000),
                        List.of(new TransactionHistoryResponse(
                                DEPOSIT.name(), null,
                                LocalDateTime.now(), BigDecimal.valueOf(100000))))
                );

        mvc.perform(get(BASE_URL + "/1986"))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.currentBalance", is(100000)))
                .andExpect(jsonPath("$.transactionHistory[0].transactionType",
                        is(DEPOSIT.name()))
                );
    }
}
