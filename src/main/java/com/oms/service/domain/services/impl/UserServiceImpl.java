package com.oms.service.domain.services.impl;

import com.ommanisoft.common.exceptions.ExceptionOm;
import com.oms.service.app.dtos.UserDto;
import com.oms.service.app.response.UserResponse;
import com.oms.service.config.JwtService;
import com.oms.service.domain.entities.Account.User;
import com.oms.service.domain.entities.Cart.Cart;
import com.oms.service.domain.entities.Role.Role;
import com.oms.service.domain.entities.Token.Token;
import com.oms.service.domain.entities.Token.TokenType;
import com.oms.service.domain.exceptions.ErrorMessageOm;
import com.oms.service.domain.repositories.Cart.CartRepository;
import com.oms.service.domain.repositories.Role.RoleRepository;
import com.oms.service.domain.repositories.TokenRepository;
import com.oms.service.domain.repositories.UserRepository;
import com.oms.service.domain.services.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.oms.service.domain.Utils.Constant.USER;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
	private final UserRepository userRepository;
	private final ModelMapper mapper;
	private final CartRepository cartRepository;
	private final RoleRepository roleRepository;
	private final PasswordEncoder passwordEncoder;

	private final AuthenticationManager authenticationManager;
	private final TokenRepository tokenRepository;
	private final JwtService jwtService;
	@Override
	@Transactional
	public UserResponse createUser(UserDto userDto) {
		User userCheckName=userRepository.findByEmailEqualsIgnoreCase(userDto.getEmail());
		if(userCheckName!=null){
			throw new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.USER_NAME_ALREADY_EXISTS.val());
		}
		Role role=roleRepository.findByNameIgnoreCase(USER);
		User user=new User();
		user.setEmail(userDto.getEmail());
		user.setDeleted(false);
		user.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
		user.getListRoles().add(role);
		user.setPassword(passwordEncoder.encode(userDto.getPassword()));
		userRepository.save(user);
		Cart cart=new Cart();
		cart.setId(user.getId());
		cart.setDeleted(false);
		cart.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
		cart.setUser(user);
		cartRepository.save(cart);

		return mapper.map(user, UserResponse.class);
	}

	@Override
	public UserResponse loginUser(UserDto userDto) {
		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userDto.getEmail(), userDto.getPassword()));
		} catch (AuthenticationException ex) {
			throw new ExceptionOm(HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessageOm.INTERNAL_SERVER_ERROR.val());
		}
		User user=userRepository.findByEmailEqualsIgnoreCase(userDto.getEmail());
		if(user==null){
			throw new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.USER_NOT_FOUND.val());
		}
		String token;
		String refreshToken;
		try {
			token = jwtService.generateToken(user, null);
			refreshToken=jwtService.generateRefreshToken(user);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
		revolkeToken(user);
		Token tokenEntity = Token.builder()
				.account(user)
				.token(token)
				.tokenType(TokenType.BEARER)
				.revolked(false)
				.refreshToken(refreshToken)
				.build();
		tokenRepository.save(tokenEntity);
		user.addToken(tokenEntity);
		UserResponse userResponse=mapper.map(user, UserResponse.class);
		userResponse.setAccessToken(token);
		userResponse.setRefreshToken(refreshToken);
		return userResponse;
	}

	@Override
	public User getAuthenticatedUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User user = (User) authentication.getPrincipal();
		String email = user.getEmail();
		User user1= userRepository.findByEmailEqualsIgnoreCase(email);
		if(user1==null){
			throw new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.USER_NOT_FOUND.val());
		}
		return user1;
	}

	@Override
	public void logout() {
		User user = getAuthenticatedUser();
		revolkeToken(user);
	}
	private void revolkeToken(User user){
		List<Token> tokenList=tokenRepository.findAllByUserId(user.getId());
		if(tokenList==null){
			return;
		}
		tokenList.forEach(token -> {
			token.setRevolked(true);
		});
		tokenRepository.saveAll(tokenList);
	}

	@Override
	public UserResponse refreshToken(String authHeader) throws NoSuchAlgorithmException {
		final String refreshToken=authHeader.substring(7);
		String email = jwtService.getUserNameRefreshToken(refreshToken);
		User user=userRepository.findByEmailEqualsIgnoreCase(email);
		if(user==null){
			throw new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.USER_NOT_FOUND.val());
		}

		Token refreshTokenCheck = tokenRepository.findByRefreshToken(refreshToken).orElseThrow(() -> new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.REFRESH_TOKEN_NOT_VALID.val()));

		if(jwtService.isValidRefreshToken(user,refreshToken)){
			revolkeToken(user);
			String tokenNew = jwtService.generateToken(user, null);
			String refreshTokenNew=jwtService.generateRefreshToken(user);
			Token tokenEntity = Token.builder()
					.account(user)
					.token(tokenNew)
					.tokenType(TokenType.BEARER)
					.revolked(false)
					.refreshToken(refreshTokenNew)
					.build();
			tokenRepository.save(tokenEntity);
			user.addToken(tokenEntity);
			UserResponse userResponse=mapper.map(user, UserResponse.class);
			userResponse.setAccessToken(tokenNew);
			userResponse.setRefreshToken(refreshTokenNew);
			return userResponse;
		}
		throw new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.REFRESH_TOKEN_NOT_VALID.val());
	}
}

