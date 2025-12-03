package com.munni.telegram_app_backend.personnel.user;


import com.munni.telegram_app_backend.exception.CustomException;
import com.munni.telegram_app_backend.personnel.role.RoleRepo;
import com.munni.telegram_app_backend.util.PaginationUtil;
import com.munni.telegram_app_backend.util.ResponseUtils;
import com.munni.telegram_app_backend.util.response.BaseApiResponseDTO;
import com.munni.telegram_app_backend.util.response.CustomPageResponseDTO;
import jakarta.transaction.Transactional;
import org.apache.catalina.security.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

	@Autowired
	private UserRepo usersRepo;

	@Autowired private UserRepo userRepo;
	@Autowired private UserMapper userMapper;
	@Autowired private RoleRepo roleRepo;
	@Autowired private SecurityUtil securityUtil;

	private final PasswordEncoder passwordEncoder;

	public UserService(@Lazy PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}

	public ResponseEntity<BaseApiResponseDTO<?>> getAll(String search, Pageable pageable) {
		Page<UserResDto> users;
		if (search != null && !search.isBlank()) {
			users = userRepo.search(search, pageable).map(userMapper::toDto);
		} else {
			users = userRepo.findAllExcept(pageable).map(userMapper::toDto);
		}

		CustomPageResponseDTO<UserResDto> paginatedResponse = PaginationUtil.buildPageResponse(users, pageable);

		return ResponseUtils.SuccessResponseWithData(paginatedResponse);
	}

	public ResponseEntity<BaseApiResponseDTO<?>> getById(Long id) {
		User user = userRepo.findById(id)
				.orElseThrow(() -> new CustomException("User not found with id: " + id, HttpStatus.NOT_FOUND));

		return ResponseUtils.SuccessResponseWithData(userMapper.toDto(user));
	}

	@Transactional
	public ResponseEntity<BaseApiResponseDTO<?>> create(UserReqDTO dto) {
		// Check if username or email already exists
		if (userRepo.existsByUserName(dto.getUserName())) {
			throw new CustomException("Username already exists: " + dto.getUserName(), HttpStatus.BAD_REQUEST);
		}
		if (userRepo.existsByEmail(dto.getEmail())) {
			throw new CustomException("Email already exists: " + dto.getEmail(), HttpStatus.BAD_REQUEST);
		}

		Role role = roleRepo.findById(dto.getRoleId())
				.orElseThrow(() -> new CustomException("Role not found with id: " + dto.getRoleId(), HttpStatus.NOT_FOUND));

		User user = userMapper.toEntity(dto);
		user.setRole(role);
		user.setIsActive(true);

		userRepo.save(user);

		return ResponseUtils.SuccessResponseWithData(userMapper.toDto(user));
	}

	@Transactional
	public ResponseEntity<BaseApiResponseDTO<?>> update(Long id, UserReqDTO dto) {
		User user = userRepo.findById(id)
				.orElseThrow(() -> new CustomException("User not found with id: " + id, HttpStatus.NOT_FOUND));

		// Check if username or email already exists for other users
		if (dto.getUserName() != null && !dto.getUserName().equals(user.getUserName())) {
			if (userRepo.existsByUserName(dto.getUserName())) {
				throw new CustomException("Username already exists: " + dto.getUserName(), HttpStatus.BAD_REQUEST);
			}
		}
		if (dto.getEmail() != null && !dto.getEmail().equals(user.getEmail())) {
			if (userRepo.existsByEmail(dto.getEmail())) {
				throw new CustomException("Email already exists: " + dto.getEmail(), HttpStatus.BAD_REQUEST);
			}
		}

		Role role = roleRepo.findById(dto.getRoleId())
				.orElseThrow(() -> new CustomException("Role not found with id: " + dto.getRoleId(), HttpStatus.NOT_FOUND));

		userMapper.updateEntityFromDto(dto, user);

		// Only update password if provided
		if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
			user.setPassword(passwordEncoder.encode(dto.getPassword()));
		}

		user.setRole(role);

		User updated = userRepo.save(user);

		return ResponseUtils.SuccessResponseWithData(userMapper.toDto(updated));
	}

	@Transactional
	public ResponseEntity<BaseApiResponseDTO<?>> updateStatus(Long id, Boolean isActive) {
		User user = userRepo.findById(id)
				.orElseThrow(() -> new CustomException("User not found with id: " + id, HttpStatus.NOT_FOUND));

		user.setIsActive(isActive);
		userRepo.save(user);

		return ResponseUtils.SuccessResponse(
				isActive ? "User activated successfully!" : "User deactivated successfully!",
				HttpStatus.OK
		);
	}

	public ResponseEntity<BaseApiResponseDTO<?>> delete(Long id) {
		User user = userRepo.findById(id)
				.orElseThrow(() -> new CustomException("User not found with id: " + id, HttpStatus.NOT_FOUND));

		userRepo.delete(user);

		return ResponseUtils.SuccessResponse("User has been deleted", HttpStatus.OK);
	}

	@Override
	public UserDetails loadUserByUsername(String input) throws UsernameNotFoundException {

		if (input == null || input.isBlank()) throw new CustomException("Username or email is required", HttpStatus.BAD_REQUEST);

		Optional<User> userOp = usersRepo.findByUserNameIgnoreCase(input);

		if (userOp.isEmpty()) {
			userOp = usersRepo.findByEmailIgnoreCase(input);
		}

		if (userOp.isEmpty()) throw new CustomException("User not found with username/email: " + input, HttpStatus.NOT_FOUND);

		User user = userOp.get();

		if (Boolean.FALSE.equals(user.getIsActive())) throw new CustomException("User is inactive", HttpStatus.UNAUTHORIZED);

		return new UserDetailsImpl(user);
	}
}
