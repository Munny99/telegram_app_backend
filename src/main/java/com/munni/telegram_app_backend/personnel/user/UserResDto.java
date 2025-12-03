package com.munni.telegram_app_backend.personnel.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Monaum Hossain
 * monaum.202@gmail.com
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResDto {


    private Long id;
    private String telegramId;

    private String firstName;
    private String lastName;
    private String userName;

    private String referralCode;

    private BigDecimal totalEarnings;
    private Long totalWithdrawn;
    private Long pendingBalance;

    private Integer totalReferrals;
    private Integer completedTasks;
    private Integer totalTasks;

    private Integer rating;

    private Boolean isActive;

    private Long referredById;
    private String referredByUserName;

    private List<Long> taskIds;
    private List<Long> withdrawalIds;
    private List<Long> myReferralIds;
}
