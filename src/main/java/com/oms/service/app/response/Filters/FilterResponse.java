package com.oms.service.app.response.Filters;

import lombok.*;
import org.apache.catalina.LifecycleState;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FilterResponse {
	private Long id;
	private String name;
	private Integer filterIndex;
	private Long attributeId;
	private String attributeName;

	private List<FilterItemResponse> listFilterItems;
}
