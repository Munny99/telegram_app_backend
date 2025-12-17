package com.munni.telegram_app_backend.module.referral;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReferralReqDto {


    @NotNull(message = "User ID is required")
    private Long userId;
}
