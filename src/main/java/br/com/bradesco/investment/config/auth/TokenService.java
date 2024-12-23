package br.com.bradesco.investment.config.auth;

import br.com.bradesco.investment.app.dto.response.TokenResponse;
import br.com.bradesco.investment.data.model.Account;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.security.KeyStore;
import java.security.PublicKey;
import java.util.Date;

@Service
public class TokenService {

    private static final String ISSUER = "BRADESCO-INVESTING";
    static final String BEARER = "Bearer";

    @Value("${bbdc.jwt.expiration}")
    String expiration;
    final KeyStore.PrivateKeyEntry keyEntry;

    public TokenService(KeyStore.PrivateKeyEntry keyEntry) {
        this.keyEntry = keyEntry;
    }

    public TokenResponse generateToken(Authentication authentication) {
        Account account = (Account) authentication.getPrincipal();
        Date issuedDate = new Date();
        Date expirationDate = new Date(issuedDate.getTime() + Long.parseLong(expiration));

        String token = Jwts.builder().issuer(ISSUER)
                .subject(account.getAccountNumber().toString())
                .issuedAt(issuedDate).expiration(expirationDate)
                .signWith(keyEntry.getPrivateKey(), Jwts.SIG.RS512)
                .compact();

        return new TokenResponse(token, BEARER, account.getAccountNumber(), issuedDate, expirationDate);
    }

    public boolean isValidToken(String token) {
        try {
            Jwts.parser().verifyWith(getPublicKey()).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Long getSubject(String token) {
        var subject = Jwts.parser().verifyWith(getPublicKey()).build().parseSignedClaims(token)
                .getPayload().getSubject();
        return Long.parseLong(subject);
    }

    private PublicKey getPublicKey() {
        return keyEntry.getCertificate().getPublicKey();
    }
}
