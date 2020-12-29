package com.hanul.caramelhomecchiato.task;

import org.apache.http.entity.mime.MultipartEntityBuilder;

public class JoinWithPhoneNumberTask<CONTEXT> extends JsonResponseTask<CONTEXT> {
	private final String name;
	private final String phoneNumber;
	private final String password;

	public JoinWithPhoneNumberTask(CONTEXT context, String name, String phoneNumber, String password){
		super(context, "joinWithPhoneNumber");
		this.name = name;
		this.phoneNumber = phoneNumber;
		this.password = password;
	}

	@Override protected void appendMultipartEntity(MultipartEntityBuilder builder){
		builder.addTextBody("name", name)
				.addTextBody("phoneNumber", phoneNumber)
				.addTextBody("password", password);
	}
}

