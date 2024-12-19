package com.oms.service.domain.services;

import com.oms.service.app.dtos.UserDto;
import com.oms.service.app.response.UserResponse;
import com.oms.service.domain.entities.Account.User;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;

public interface UserService {
	UserResponse createUser(UserDto userDto);
	UserResponse loginUser(UserDto userDto);
	void logout();
	UserResponse refreshToken(String authHeader) throws NoSuchAlgorithmException;

	User getAuthenticatedUser();

}
