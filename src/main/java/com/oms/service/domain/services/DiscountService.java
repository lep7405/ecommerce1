package com.oms.service.domain.services;

import com.oms.service.app.dtos.Discount.DiscountCombo.ProgramDiscountComboDto;
import com.oms.service.app.dtos.Discount.DiscountGift.ProgramDiscountGiftDto;
import com.oms.service.app.dtos.Discount.DiscountHotSaleIDto.ProgramDiscountHotSaleDto;
import com.oms.service.app.response.DiscountCombo.ProgramDiscountResponse;
import com.oms.service.app.response.DiscountCombo1.ProgramDiscountCombo1Response;
import com.oms.service.app.response.DiscountHotSale.ProgramDiscountHotSaleResponse;
import com.oms.service.domain.entities.Discount.ProgramDiscount;

public interface DiscountService {
	ProgramDiscount createDiscountHotSale(ProgramDiscountHotSaleDto programDiscountHotSaleDto);
	ProgramDiscountResponse createDiscountComboGift(ProgramDiscountGiftDto programDiscountComboDto);
	ProgramDiscountResponse getProgramDiscountCombo(Long id);

	ProgramDiscountHotSaleResponse getProgramDiscountHotSale(Long id);
	ProgramDiscountCombo1Response createDisCountCombo(ProgramDiscountComboDto programDiscountComboDto);
	ProgramDiscountCombo1Response getProgramDiscountCombo1(Long id);
}
