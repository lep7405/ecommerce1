package com.oms.service.domain.repositories.Cart;

import com.oms.service.domain.entities.Cart.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CartRepository extends JpaRepository<Cart,Long> {
	@Query("SELECT c FROM Cart c JOIN FETCH c.listCartItems ci JOIN FETCH ci.product p JOIN FETCH ci.productVariant pv WHERE c.id = :id")
	Cart findCartWithItemsIncludingDeletedProducts(@Param("id") Long id);

	@Query("SELECT c FROM Cart c JOIN FETCH c.listCartItems ci JOIN FETCH ci.product p JOIN FETCH ci.productVariant pv WHERE c.user.id = :userId")
	Cart findCartByUserId(@Param("userId") Long userId);

}
