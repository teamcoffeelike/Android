package com.hanul.caramelhomecchiato.task;

import org.apache.http.entity.mime.MultipartEntityBuilder;

public class JoinWithEmailTask<CONTEXT> extends JsonResponseTask<CONTEXT>{
	private final String name;
	private final String email;
	private final String password;

	public JoinWithEmailTask(CONTEXT context, String name, String email, String password){
		super(context, "joinWithEmail");
		this.name = name;
		this.email = email;
		this.password = password;
	}

	@Override protected void appendMultipartEntity(MultipartEntityBuilder builder){
		builder.addTextBody("name", name)
				.addTextBody("email", email)
				.addTextBody("password", password);
	}
}