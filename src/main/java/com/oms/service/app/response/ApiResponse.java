package com.oms.service.app.response;

import io.swagger.annotations.Api;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiResponse <T>{
	private int status;
	private T data;

	public ApiResponse(int status, T data) {
		this.status = status;
		this.data = data;
	}
	public ApiResponse(int status){
		this.status=status;
	}
}

