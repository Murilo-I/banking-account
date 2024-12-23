package br.com.bradesco.investment.app.controller;

import br.com.bradesco.investment.data.model.Account;
import br.com.bradesco.investment.data.model.AccountAccess;
import br.com.bradesco.investment.data.repository.AccountAccessRepository;
import br.com.bradesco.investment.data.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.math.BigInteger;

import static br.com.bradesco.investment.app.controller.AccountControllerTest.HOLDOR;
import static br.com.bradesco.investment.data.model.AccountType.BASIC_ACCOUNT;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthenticationControllerTest {

    static final String EMAIL = "murilo.tegani@email.com";
    static final String STRONG = "strong";

    @Autowired
    MockMvc mvc;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    AccountAccessRepository accessRepository;

    @BeforeEach
    void setup() {
        var createdAccess = accessRepository.save(
                AccountAccess.builder()
                        .email(EMAIL)
                        .password(new BCryptPasswordEncoder().encode(STRONG))
                        .build()
        );
        accountRepository.save(
                Account.builder()
                        .holderName(HOLDOR)
                        .balance(new BigDecimal(BigInteger.ZERO))
                        .access(createdAccess)
                        .accountType(BASIC_ACCOUNT)
                        .build()
        );
    }

    @Test
    @WithMockUser("test-account")
    void testAuthenticate() throws Exception {
        var payload = """
                {
                    "email": "%s",
                    "password": "%s"
                }
                """.formatted(EMAIL, STRONG);

        mvc.perform(post("/auth")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(payload)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.type", is("Bearer")));
    }
}
