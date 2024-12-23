package br.com.bradesco.investment.config.auth;

import br.com.bradesco.investment.app.usecase.AccountDataUseCase;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static br.com.bradesco.investment.config.auth.TokenService.BEARER;

@Component
public class AuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION = "Authorization";
    private static final String ORIGIN_ACCOUNT_NUMBER = "originAccountNumber";
    private static final long ZERO = 0L;

    final TokenService tokenService;
    final AccountDataUseCase useCase;

    public AuthenticationFilter(TokenService tokenService, AccountDataUseCase useCase) {
        this.tokenService = tokenService;
        this.useCase = useCase;
    }


    @Override
    @SuppressWarnings("NullableProblems")
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        var token = getToken(request);

        if (tokenService.isValidToken(token)) authenticate(request, token);

        filterChain.doFilter(request, response);
    }

    private void authenticate(HttpServletRequest request, String token) {
        Long accountIdOnToken = tokenService.getSubject(token);
        Long accountIdOnPath = extractAccountIdFromPath(request.getRequestURI());
        Long accountIdFromParam = extractAccountIdFromReqParam(
                request.getParameter(ORIGIN_ACCOUNT_NUMBER)
        );

        if (accountIdOnPath.equals(accountIdOnToken)
                || accountIdFromParam.equals(accountIdOnToken)) {
            useCase.findByAccountNumber(accountIdOnToken).ifPresent(account -> {
                var authToken = new UsernamePasswordAuthenticationToken(
                        account, null, account.getAuthorities()
                );
                SecurityContextHolder.getContext().setAuthentication(authToken);
            });
        }
    }

    private Long extractAccountIdFromPath(String path) {
        try {
            String[] splits = path.split("/");
            return Long.parseLong(splits[splits.length - 1]);
        } catch (NumberFormatException e) {
            return ZERO;
        }
    }

    private Long extractAccountIdFromReqParam(String param) {
        try {
            return Long.parseLong(param);
        } catch (NumberFormatException e) {
            return ZERO;
        }
    }

    private String getToken(HttpServletRequest request) {
        String token = request.getHeader(AUTHORIZATION);

        if (token == null || !token.startsWith(BEARER))
            return null;

        return token.substring(7);
    }
}
