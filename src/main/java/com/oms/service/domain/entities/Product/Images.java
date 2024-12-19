package com.oms.service.domain.entities.Product;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Images {
  @NotBlank(message = "Image url is required")
  @Size(max = 255, message = "Image url must be less than 255 characters")
  private String url;

  @NotNull(message = "Image isCover is required")
  private Boolean isCover;
}
