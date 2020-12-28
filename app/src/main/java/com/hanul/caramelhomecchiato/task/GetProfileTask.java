package com.hanul.caramelhomecchiato.task;

import org.apache.http.entity.mime.MultipartEntityBuilder;

public class GetProfileTask<CONTEXT> extends JsonResponseTask<CONTEXT>{
	private final int userId;

	public GetProfileTask(CONTEXT context, int userId){
		super(context, "profile");
		this.userId = userId;
	}

	@Override protected void appendMultipartEntity(MultipartEntityBuilder builder){
		builder.addTextBody("userId", Integer.toString(userId));
	}
}
