package com.hanul.caramelhomecchiato.task;

import org.apache.http.entity.mime.MultipartEntityBuilder;

public class SearchFriendTask<CONTEXT> extends JsonResponseTask<CONTEXT> {
	public SearchFriendTask(CONTEXT context) {
		super(context, "getUsers");
	}

	@Override
	protected void appendMultipartEntity(MultipartEntityBuilder builder) {

	}
}
