package com.munni.telegram_app_backend.referral;

import com.munni.telegram_app_backend.enums.ReferralStatus;
import com.munni.telegram_app_backend.personnel.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReferralRepo extends JpaRepository<Referral ,Long> {

    List<Referral> findByReferrer(User referrer);
    List<Referral> findByReferee(User referee);
    long countByReferrer(User referrer);
    long countByReferrerAndStatus(User referrer, ReferralStatus status);
    Optional<Referral> findByReferrerAndReferee(User referrer, User referee);
}
