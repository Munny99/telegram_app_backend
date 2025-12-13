package com.munni.telegram_app_backend.withdrawal;

import org.springframework.data.jpa.repository.JpaRepository;

public interface WithdrawalRepo extends JpaRepository<Withdrawal , Long> {
}
