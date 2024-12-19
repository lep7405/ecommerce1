package com.oms.service.domain.repositories.Cart;

import com.oms.service.domain.entities.Cart.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem,Long> {
	@Query("SELECT ci FROM CartItem ci WHERE ci.cart.id = :cartId ORDER BY ci.createdAt DESC")
	List<CartItem> findCartItemsByCartIdSorted(@Param("cartId") Long cartId);

	@Modifying
	@Query("UPDATE CartItem ci SET ci.deleted = :deleted WHERE ci.id IN :ids")
	void saveAllById(@Param("ids") List<Long> ids, @Param("deleted") Boolean deleted);
}
