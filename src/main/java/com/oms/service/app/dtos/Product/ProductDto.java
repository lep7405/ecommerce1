package com.oms.service.app.dtos.Product;
import com.oms.service.app.dtos.ProductVariantDto;
import com.oms.service.domain.entities.Commitment;
import com.oms.service.domain.entities.Product.Images;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.internal.bytebuddy.implementation.bind.annotation.Empty;


import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto {

    @NotBlank(message = "Name is required")
    @Size(min = 1, max = 225, message = "Name must be less than 225 characters")
    private String name;
	@NotBlank(message = "Description is required")
	private String description;

    @NotNull(message = "Image is required")
    private List<Images> images;

    @NotEmpty(message = "Commitment is required")
    private List<Commitment> listCommitment;

    @NotNull(message = "Category id is required")
    private Long categoryId;
    @NotNull(message = "brandValueId is required")
    private Long brandValueId;
    @NotNull(message = "typeProductValueId is required")
    private Long typeProductValueId;

    @NotNull(message = "max1Buy is required")
    @Min(value = 1, message = "max1Buy must be greater than or equal to 1")
    @Max(value = 20, message = "max1Buy must be less than or equal to 20")
    private Integer max1Buy;

    private String state;

    @Valid
    @NotEmpty(message = "listProductVariantsDto is required")
    private List<ProductVariantDto> listProductVariantsDto;

    @Valid
    @NotNull(message = "listProductVariantsDto is required")
    List<ParameterDto1> listParameterDto;
}

