package com.oms.service.domain.entities.Account;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.oms.service.domain.entities.Role.Role;
import com.oms.service.domain.entities.Token.Token;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.*;

@Entity
@Getter
@Setter
@Inheritance(strategy = InheritanceType.JOINED)
public class Account implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @Column(name = "email",unique = true)
    private String email;
    @Column(name = "password")
    private String password;

    @Column(name="phone")
    private String phone;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp updatedAt;

    @Column(name="deleted")
    private boolean deleted;

    @OneToMany(mappedBy = "account",fetch = FetchType.LAZY)
    private List<Token> tokenList;
    public void addToken(Token token){
        if(tokenList==null){
            tokenList=new ArrayList<>();
        }
        tokenList.add(token);
        token.setAccount(this);
    }

    @ManyToMany(
            fetch = FetchType.EAGER,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
    @JoinTable(
            name = "rel_account_role",
            joinColumns = @JoinColumn(name = "account_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    @JsonManagedReference
    private List<Role> listRoles=new ArrayList<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        // Thêm role authorities
        List<SimpleGrantedAuthority> roleAuthorities = listRoles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
                .toList();
        Set<SimpleGrantedAuthority> authorities = new HashSet<>(roleAuthorities);

        // Thêm permission từ mỗi role
        listRoles.forEach(role -> {
            List<SimpleGrantedAuthority> rolePermissions = role.getListPermissions().stream()
                    .map(permission -> new SimpleGrantedAuthority("PERMISSION_" + permission.getName()))
                    .toList();
            authorities.addAll(rolePermissions);
        });

        return authorities;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
