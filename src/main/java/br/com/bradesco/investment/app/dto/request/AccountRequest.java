package br.com.bradesco.investment.app.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class AccountRequest {
    @NotBlank(message = "Holder name cannot be blank.")
    String holderName;
    @Email(message = "Invalid e-mail format.")
    @NotBlank(message = "E-mail cannot be blank.")
    String email;
    @Size(min = 8, max = 25, message = "Password length must be between 8 and 25.")
    @NotBlank(message = "Password is required.")
    String password;
}
