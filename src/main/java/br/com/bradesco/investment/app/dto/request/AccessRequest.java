package br.com.bradesco.investment.app.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class AccessRequest {
    @NotBlank(message = "E-mail cannot be blank.")
    String email;
    @NotBlank(message = "Password cannot be blank.")
    String password;
}
