package com.hanul.caramelhomecchiato.task;

import org.apache.http.entity.mime.MultipartEntityBuilder;

public class LoginWithAuthTokenTask<CONTEXT> extends JsonResponseTask<CONTEXT>{
	private final String authToken;

	public LoginWithAuthTokenTask(CONTEXT context, String authToken){
		super(context, "loginWithAuthToken");
		this.authToken = authToken;
	}

	@Override protected void appendMultipartEntity(MultipartEntityBuilder builder){
		builder.addTextBody("authToken", authToken);
	}
}
