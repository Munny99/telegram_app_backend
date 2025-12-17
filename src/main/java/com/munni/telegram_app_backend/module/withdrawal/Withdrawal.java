package com.munni.telegram_app_backend.module.withdrawal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.munni.telegram_app_backend.module.user.User;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Withdrawal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;
}
