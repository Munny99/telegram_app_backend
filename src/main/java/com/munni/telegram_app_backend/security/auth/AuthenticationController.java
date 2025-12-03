package com.munni.telegram_app_backend.security.auth;

import com.munni.telegram_app_backend.enums.ResponseStatusType;
import com.munni.telegram_app_backend.model.ResponseBuilder;
import com.munni.telegram_app_backend.model.SuccessResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * Monaum Hossain
 * @since jul 18, 2025
 */

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

	private @Autowired AuthenticationService authenticationService;

	@PostMapping("/register")
	public ResponseEntity<SuccessResponse<AuthenticationResDto>> register(@RequestBody RegisterRequestDto request) {
		AuthenticationResDto data = authenticationService.register(request);
		return ResponseBuilder.build(ResponseStatusType.CREATE_SUCCESS, data);
	}

	@PostMapping("/authenticate")
	public ResponseEntity<SuccessResponse<AuthenticationResDto>> authenticate(@RequestBody AuthenticationReqDto request) {
		AuthenticationResDto data = authenticationService.authenticate(request);
		return ResponseBuilder.build(ResponseStatusType.READ_SUCCESS, data);
	}

	@PostMapping("/refresh-token")
	public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
		authenticationService.refreshToken(request, response);
	}
}
