package com.hanul.caramelhomecchiato.task;

import org.apache.http.entity.mime.MultipartEntityBuilder;

public class LoginWithPhoneNumberTask<CONTEXT> extends JsonResponseTask<CONTEXT>{
	private final String phoneNumber;
	private final String password;

	public LoginWithPhoneNumberTask(CONTEXT context, String phoneNumber, String password){
		super(context, "loginWithPhoneNumber");
		this.phoneNumber = phoneNumber;
		this.password = password;
	}

	@Override protected void appendMultipartEntity(MultipartEntityBuilder builder){
		builder.addTextBody("phoneNumber", phoneNumber).addTextBody("password", password);
	}
}
