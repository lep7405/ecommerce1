package com.oms.service.config;

import io.github.cdimascio.dotenv.Dotenv;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {
    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expirationMs}")
    private long jwtExpirationMs;

    @Value("${jwt.refreshSecret}")
    private String jwtRefreshSecret;

    @Value("${jwt.refreshExpirationMs}")
    private long jwtRefreshExpirationMs;


    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String buildToken(UserDetails userDetails,Map<String,Object> claims,long expiration) {
        if (claims == null) {
            claims = new HashMap<>();
        }
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+expiration*1000))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    public String generateToken(UserDetails userDetails, Map<String,Object> claims) throws NoSuchAlgorithmException {
        return buildToken(userDetails,claims,jwtExpirationMs);
    };
    public String generateRefreshToken(UserDetails userDetails) throws NoSuchAlgorithmException {
        return buildToken(userDetails,null,jwtRefreshExpirationMs);
    }
    //giải token
    public Claims extraToken(String token)  {
        return Jwts.parserBuilder().setSigningKey((getSignInKey())).build().parseClaimsJws(token).getBody();
    }
    public Claims extraRefreshToken(String token) throws NoSuchAlgorithmException {
        return Jwts.parserBuilder().setSigningKey(getSignInKey()).build().parseClaimsJws(token).getBody();
    }
    public String getUserNameToken(String token)   {
        return extraToken(token).getSubject();
    }
    public Date getExpirationToken(String token)   {
        return extraToken(token).getExpiration();
    }
    public boolean isExpirationToken(String token)   {
        return getExpirationToken(token).after(new Date());
    }

    public boolean isValidToken(UserDetails userDetails,String token)   {
        return userDetails.getUsername().equals(getUserNameToken(token))&&isExpirationToken(token);
    }


    public String getUserNameRefreshToken(String refreshToken) throws NoSuchAlgorithmException {
        return extraRefreshToken(refreshToken).getSubject();
    }
    public Date getExpirationRefreshToken(String refreshToken) throws NoSuchAlgorithmException {
        return extraRefreshToken(refreshToken).getExpiration();
    }
    public boolean isExpirationRefreshToken(String refreshToken) throws NoSuchAlgorithmException {
        return getExpirationRefreshToken(refreshToken).after(new Date());
    }

    public boolean isValidRefreshToken(UserDetails userDetails,String refreshToken) throws NoSuchAlgorithmException {
        return userDetails.getUsername().equals(getUserNameRefreshToken(refreshToken))&&isExpirationRefreshToken(refreshToken);
    }

}
