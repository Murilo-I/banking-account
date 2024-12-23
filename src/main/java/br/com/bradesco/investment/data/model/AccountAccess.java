package br.com.bradesco.investment.data.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Entity
@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AccountAccess implements Serializable {

    @Serial
    private static final long serialVersionUID = 1905122041950251208L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long accessId;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;
}
