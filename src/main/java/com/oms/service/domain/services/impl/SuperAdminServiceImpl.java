package com.oms.service.domain.services.impl;

import com.ommanisoft.common.exceptions.ExceptionOm;
import com.oms.service.app.dtos.SuperAdminDto;
import com.oms.service.app.response.SuperAdminResponse;
import com.oms.service.config.JwtService;
import com.oms.service.domain.entities.Account.SuperAdmin;
import com.oms.service.domain.entities.Role.Role;
import com.oms.service.domain.entities.Token.Token;
import com.oms.service.domain.entities.Token.TokenType;
import com.oms.service.domain.exceptions.ErrorMessageOm;
import com.oms.service.domain.repositories.Role.RoleRepository;
import com.oms.service.domain.repositories.SuperAdminRepository;
import com.oms.service.domain.repositories.TokenRepository;
import com.oms.service.domain.services.SuperAdminService;
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

import java.security.NoSuchAlgorithmException;
import java.util.List;

import static com.oms.service.domain.Utils.Constant.SUPER_ADMIN;

@Service
@RequiredArgsConstructor
public class SuperAdminServiceImpl implements SuperAdminService {
	private final SuperAdminRepository superAdminRepository;
	private final PasswordEncoder passwordEncoder;
	private final ModelMapper modelMapper;
	private final RoleRepository roleRepository;

	private final AuthenticationManager authenticationManager;
	private final JwtService jwtService;
	private final TokenRepository tokenRepository;

	@Override
	public SuperAdminResponse createSuperAdmin(SuperAdminDto superAdminDto) {
		SuperAdmin superAdminCheck=superAdminRepository.findByEmailEqualsIgnoreCase(superAdminDto.getEmail());
		if(superAdminCheck!=null){
			throw new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.EXIST_EMAIL.val());
		}
		Role role=roleRepository.findByNameIgnoreCase(SUPER_ADMIN);
		SuperAdmin superAdmin=new SuperAdmin();
		superAdmin.setEmail(superAdminDto.getEmail());
		superAdmin.setPassword(passwordEncoder.encode(superAdminDto.getPassword()));
		superAdmin.getListRoles().add(role);
		superAdminRepository.save(superAdmin);
		return modelMapper.map(superAdmin, SuperAdminResponse.class);
	}

	@Override
	public SuperAdminResponse loginSuperAdmin(SuperAdminDto superAdminDto) {
		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(superAdminDto.getEmail(), superAdminDto.getPassword()));
		} catch (AuthenticationException ex) {
			throw new ExceptionOm(HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessageOm.INTERNAL_SERVER_ERROR.val());
		}
		SuperAdmin superAdmin=superAdminRepository.findByEmailEqualsIgnoreCase(superAdminDto.getEmail());
		if(superAdmin==null){
			throw new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.USER_NOT_FOUND.val());
		}
		String token;
		String refreshToken;
		try {
			token = jwtService.generateToken(superAdmin, null);
			refreshToken=jwtService.generateRefreshToken(superAdmin);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
		revolkeToken(superAdmin);
		Token tokenEntity = Token.builder()
				.account(superAdmin)
				.token(token)
				.tokenType(TokenType.BEARER)
				.revolked(false)
				.refreshToken(refreshToken)
				.build();
		tokenRepository.save(tokenEntity);
		superAdmin.addToken(tokenEntity);
		SuperAdminResponse superAdminResponse=modelMapper.map(superAdmin, SuperAdminResponse.class);
		superAdminResponse.setAccessToken(token);
		superAdminResponse.setRefreshToken(refreshToken);
		return superAdminResponse;
	}

	private void revolkeToken(SuperAdmin superAdmin){
		List<Token> tokenList=tokenRepository.findAllByUserId(superAdmin.getId());
		if(tokenList==null){
			return;
		}
		tokenList.forEach(token -> {
			token.setRevolked(true);
		});
		tokenRepository.saveAll(tokenList);
	}

	@Override
	public SuperAdmin getAuthenticatedSuperAdmin() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		SuperAdmin superAdmin = (SuperAdmin) authentication.getPrincipal();
		String email = superAdmin.getEmail();
		SuperAdmin superAdmin1= superAdminRepository.findByEmailEqualsIgnoreCase(email);
		if(superAdmin1==null){
			throw new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.USER_NOT_FOUND.val());
		}
		return superAdmin1;
	}

	@Override
	public void logout() {
		SuperAdmin superAdmin = getAuthenticatedSuperAdmin();
		revolkeToken(superAdmin);
	}

}
