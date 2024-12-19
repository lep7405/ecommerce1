package com.oms.service.app.controllers;

import com.oms.service.app.dtos.UserDto;
import com.oms.service.app.response.ApiResponse;
import com.oms.service.app.response.UserResponse;
import com.oms.service.domain.entities.Account.User;
import com.oms.service.domain.repositories.UserRepository;
import com.oms.service.domain.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.NoSuchAlgorithmException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@Slf4j
public class UserController {
	private final UserService userService;
	private final UserRepository userRepository;

	@PostMapping
	public ApiResponse<UserResponse> createUser(@RequestBody @Valid UserDto userDto){
		UserResponse userResponse=userService.createUser(userDto);
		return new ApiResponse<>(HttpStatus.OK.value(),userResponse);
	}

	@PostMapping("/login")
	public ApiResponse<UserResponse> login(@RequestBody @Valid UserDto userDto) {
		UserResponse userResponse=userService.loginUser(userDto);
		return new ApiResponse<>(HttpStatus.OK.value(),userResponse);
	}
	@GetMapping("/{id}")
	public User getUser(@PathVariable Long id) {
		User user=userRepository.getById(id);
		return user;
	}

	@PostMapping("/logout")
	public ApiResponse<?> logout(){
		userService.logout();
		return new ApiResponse<>(HttpStatus.NO_CONTENT.value());
	}

	@PostMapping("/refresh-token")
	public ApiResponse<UserResponse> refreshToken(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) throws NoSuchAlgorithmException {
		UserResponse userResponse=userService.refreshToken(authHeader);
		return new ApiResponse<>(HttpStatus.OK.value(),userResponse);
	}

}
