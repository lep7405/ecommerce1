package com.oms.service.config;

import com.ommanisoft.common.exceptions.ExceptionOm;
import com.oms.service.domain.entities.Account.Admin;
import com.oms.service.domain.entities.Account.User;
import com.oms.service.domain.exceptions.ErrorMessageOm;
import com.oms.service.domain.repositories.AdminRepository;
import com.oms.service.domain.repositories.UserRepository;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {
    private final UserRepository userRepository;
    private final AdminRepository adminRepository;

//    @Bean
//    public JavaMailSender javaMailSender() {
//        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
//        mailSender.setHost("smtp.gmail.com");
//        mailSender.setPort(587);
//        mailSender.setUsername("lep7402@gmail.com");
//        mailSender.setPassword("owypgfygclstqzrx");
//
//        Properties props = mailSender.getJavaMailProperties();
//        props.put("mail.smtp.auth", "true");
//        props.put("mail.smtp.starttls.enable", "true");
//        props.put("mail.smtp.starttls.required", "true"); // Đảm bảo rằng TLS là bắt buộc
//        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
//
//        return mailSender;
//    }
    @Bean
    public UserDetailsService userDetailsService() {
        return email -> {
            User user = userRepository.findByEmailEqualsIgnoreCase(email);

            if(user!=null){
                return user;
            }
            else{
                Admin admin = adminRepository.findByEmailEqualsIgnoreCase(email);
                if(admin!=null){
                    return admin;
                }
                throw new ExceptionOm(HttpStatus.BAD_REQUEST, ErrorMessageOm.NOT_FOUND_EMAIL);
            }
        };
    }


    @Bean
    public AuthenticationProvider authenticationProvider() {
        Logger logger = LoggerFactory.getLogger(ApplicationConfig.class);
        logger.info("pro111");
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        logger.info("pro6"+authProvider.getUserCache());
        return authProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}


