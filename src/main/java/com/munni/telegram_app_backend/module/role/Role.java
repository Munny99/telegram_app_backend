package com.munni.telegram_app_backend.module.role;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.munni.telegram_app_backend.module.user.User;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Monaum Hossain
 * monaum.202@gmail.com
 * @since 14-Nov-25 11:56 PM
 */

@Entity
@Table(name = "role")
@EntityListeners(AuditingEntityListener.class)
@Data
public class Role {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(nullable = false, updatable = false)
    private String name;

    @Column
    private String description;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime dateCreated;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime lastUpdated;

    @OneToMany(mappedBy = "role", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<User> user;

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(this.name));

    }

}
