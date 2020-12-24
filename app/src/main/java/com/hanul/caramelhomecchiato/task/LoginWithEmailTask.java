package com.hanul.caramelhomecchiato.task;

import org.apache.http.entity.mime.MultipartEntityBuilder;

public class LoginWithEmailTask<CONTEXT> extends JsonResponseTask<CONTEXT>{
	private final String email;
	private final String password;

	public LoginWithEmailTask(CONTEXT context, String email, String password){
		super(context, "loginWithEmail");
		this.email = email;
		this.password = password;
	}

	@Override protected void appendMultipartEntity(MultipartEntityBuilder builder){
		builder.addTextBody("email", email).addTextBody("password", password);
	}
}