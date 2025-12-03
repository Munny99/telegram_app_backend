package com.munni.telegram_app_backend.personnel.user;

import com.munni.telegram_app_backend.task.Task;
import com.munni.telegram_app_backend.withdrawal.Withdrawal;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.boot.autoconfigure.ldap.LdapProperties;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
@ToString(onlyExplicitlyIncluded = true)
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@EqualsAndHashCode.Include
	private Long id;

	@Column(unique = true)
	private String telegramId;

	@ToString.Include
	@Column(name = "first_name", length = 50, unique = true, nullable = false)
	private String firstName;

	@ToString.Include
	@Column(name = "last_name", length = 50, unique = true, nullable = false)
	private String lastName;

	@ToString.Include
	@Column(name = "user_name", length = 50, unique = true, nullable = false)
	private String userName;

	@Column(name = "referral_code", length = 8, unique = true, nullable = false)
	private String referralCode;

	@Column(nullable = false, precision = 20, scale = 2)
	private BigDecimal totalEarnings;
	@Column(nullable = false)
	private Long totalWithdrawn = 0L;

	@Column(nullable = false)
	private Long pendingBalance = 0L;

	@Column(nullable = false)
	private Integer totalReferrals = 0;

	@Column(nullable = false)
	private Integer completedTasks = 0;

	@Column(nullable = false)
	private Integer totalTasks = 0;

	private Integer rating;


	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
	private List<Task> tasks;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
	private List<Withdrawal> withdrawals;

	@OneToMany(mappedBy = "referrer", cascade = CascadeType.ALL)
	private List<LdapProperties.Referral> myReferrals;

	@ManyToOne
	@JoinColumn(name = "referred_by_id")
	private User referredBy;

	private Boolean isActive = true;





}
