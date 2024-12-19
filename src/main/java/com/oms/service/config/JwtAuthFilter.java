package com.oms.service.config;

import com.oms.service.domain.repositories.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
	private final JwtService jwtService;
	private final UserDetailsService userDetailsService;
	private final TokenRepository tokenRepository;
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		logger.info("doFilterInternal");
		final String authHeader=request.getHeader("Authorization");
		logger.info("hello1"+authHeader);
		if(authHeader==null||!authHeader.startsWith("Bearer ")){
			filterChain.doFilter(request,response);
			return ;
		}
		logger.info("hello2");
		final String jwt=authHeader.substring(7);
		final String email= jwtService.getUserNameToken(jwt);
		logger.info("hello3");
		if(email!=null&& SecurityContextHolder.getContext().getAuthentication()==null){
			UserDetails userDetails=this.userDetailsService.loadUserByUsername(email);
			logger.info("hello4");
			var isTokenValid = tokenRepository.findByToken(jwt)
					.map(t -> !t.getRevolked())
					.orElse(false);
			logger.info("hello5");
			if(jwtService.isValidToken(userDetails,jwt)&&isTokenValid){
				if(jwtService.isExpirationToken(jwt)){
					UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken=new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
					usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
				}
			}
		}

		filterChain.doFilter(request,response);
	}


}