package com.oms.service.app.response.Filters;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FilterItemResponse {
	private Long id;
	private String name;
	private Integer mins;
	private Integer maxs;
	private Long attributeValueId;

}
