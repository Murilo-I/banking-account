package br.com.bradesco.investment.app.controller.advice;

import br.com.bradesco.investment.util.exception.AccountNotFoundException;
import br.com.bradesco.investment.util.exception.InsufficientBalanceException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class ExceptionHandlerAdvice {

    private record ErrorMessage(String errorMessage) {
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        var errors = new HashMap<String, String>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler({
            AccountNotFoundException.class,
            UsernameNotFoundException.class,
            InsufficientBalanceException.class,
    })
    ResponseEntity<ErrorMessage> handleRuntimeExceptions(RuntimeException ex) {
        return ResponseEntity.badRequest().body(new ErrorMessage(ex.getMessage()));
    }
}
