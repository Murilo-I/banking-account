package br.com.bradesco.investment.data.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

@Entity
@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Account implements UserDetails {

    @Serial
    private static final long serialVersionUID = 1905122041950251207L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long accountNumber;

    @Column(nullable = false)
    String holderName;

    @Setter
    @Column(nullable = false)
    BigDecimal balance;

    @OneToOne
    AccountAccess access;

    @Enumerated(EnumType.STRING)
    AccountType accountType;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(this.accountType);
    }

    @Override
    public String getPassword() {
        return this.access.getPassword();
    }

    @Override
    public String getUsername() {
        return this.access.getEmail();
    }
}
