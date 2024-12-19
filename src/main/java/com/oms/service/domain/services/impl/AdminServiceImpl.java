package com.oms.service.domain.services.impl;

import com.ommanisoft.common.exceptions.ExceptionOm;
import com.oms.service.app.dtos.AdminDto;
import com.oms.service.app.response.AdminResponse;
import com.oms.service.config.JwtService;
import com.oms.service.domain.entities.Account.Admin;
import com.oms.service.domain.entities.Role.Role;
import com.oms.service.domain.entities.Token.Token;
import com.oms.service.domain.entities.Token.TokenType;
import com.oms.service.domain.exceptions.ErrorMessageOm;
import com.oms.service.domain.repositories.AdminRepository;
import com.oms.service.domain.repositories.Role.RoleRepository;
import com.oms.service.domain.repositories.TokenRepository;
import com.oms.service.domain.services.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
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

import static com.oms.service.domain.Utils.Constant.ADMIN;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
	private final AdminRepository adminRepository;
	private final ModelMapper mapper;
	private final RoleRepository roleRepository;
	private final PasswordEncoder passwordEncoder;
	private final AuthenticationManager authenticationManager;
	private final JwtService jwtService;
	private final TokenRepository tokenRepository;
	@Override
	public AdminResponse createdAdmin(AdminDto adminDto) {
		Admin adminCheck=adminRepository.findByEmailEqualsIgnoreCase(adminDto.getEmail());
		if(adminCheck!=null){
			throw new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.EXIST_EMAIL.val());
		}
		Role role=roleRepository.findByNameIgnoreCase(ADMIN);
		Admin admin=new Admin();
		admin.setEmail(adminDto.getEmail());
		admin.setPassword(passwordEncoder.encode(adminDto.getPassword()));
		admin.getListRoles().add(role);
		adminRepository.save(admin);
		return mapper.map(admin, AdminResponse.class);
	}

	@Override
	public AdminResponse login(AdminDto adminDto) {
		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(adminDto.getEmail(), adminDto.getPassword()));
		} catch (AuthenticationException ex) {
			throw new ExceptionOm(HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessageOm.INTERNAL_SERVER_ERROR.val());
		}
		Admin admin=adminRepository.findByEmailEqualsIgnoreCase(adminDto.getEmail());
		if(admin==null){
			throw new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.USER_NOT_FOUND.val());
		}
		String token;
		String refreshToken;
		try {
			token = jwtService.generateToken(admin, null);
			refreshToken=jwtService.generateRefreshToken(admin);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
		revolkeToken(admin);
		Token tokenEntity = Token.builder()
				.account(admin)
				.token(token)
				.tokenType(TokenType.BEARER)
				.revolked(false)
				.refreshToken(refreshToken)
				.build();
		tokenRepository.save(tokenEntity);
		admin.addToken(tokenEntity);
		AdminResponse adminResponse=mapper.map(admin, AdminResponse.class);
		adminResponse.setAccessToken(token);
		adminResponse.setRefreshToken(refreshToken);
		return adminResponse;
	}

	private void revolkeToken(Admin admin){
		List<Token> tokenList=tokenRepository.findAllByUserId(admin.getId());
		if(tokenList==null){
			return;
		}
		tokenList.forEach(token -> {
			token.setRevolked(true);
		});
		tokenRepository.saveAll(tokenList);
	}

	@Override
	public void logout() {
		Admin admin = getAuthenticatedAdmin();
		revolkeToken(admin);
	}

	@Override
	public Admin getAuthenticatedAdmin() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Admin admin = (Admin) authentication.getPrincipal();
		String email = admin.getEmail();
		Admin admin1= adminRepository.findByEmailEqualsIgnoreCase(email);
		if(admin1==null){
			throw new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.USER_NOT_FOUND.val());
		}
		return admin1;
	}

}
