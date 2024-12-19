package com.oms.service.domain.repositories;

import com.oms.service.domain.entities.Token.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token,Long> {
	@Query("select t from Token t where t.token=:token")
	Optional<Token> findByToken(@Param("token") String token);
	@Query("select t from Token t where t.account.id=:userId")
	List<Token> findAllByUserId(Long userId);

	@Query(value = "select t from Token t where t.refreshToken=:token and t.revolked=false")
	Optional<Token> findByRefreshToken(String token);
}
