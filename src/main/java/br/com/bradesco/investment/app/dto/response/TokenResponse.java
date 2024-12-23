package br.com.bradesco.investment.app.dto.response;

import java.util.Date;

public record TokenResponse(String token, String type, Long accountId, Date issuedAt, Date expiration) {
}
