package com.oms.service.domain.entities.Token;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.oms.service.domain.entities.Account.Account;
import lombok.*;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "token")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})

public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String token;

    @Column(name = "token_type")
    @Enumerated(EnumType.STRING)
    private TokenType tokenType;

    @Column(name = "revolked")
    private Boolean revolked;

    @Column(name = "refresh_token",columnDefinition = "TEXT")
    private String refreshToken;

    @ManyToOne()
    @JoinColumn(name = "account_id")
    @JsonIgnore
    private Account account;

}
