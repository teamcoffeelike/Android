package com.hanul.caramelhomecchiato.task;

import org.apache.http.entity.mime.MultipartEntityBuilder;

public class GetFollowerTask<CONTEXT> extends JsonResponseTask<CONTEXT>{
	public GetFollowerTask(CONTEXT context){
		super(context, "getFollower");
	}

	@Override protected void appendMultipartEntity(MultipartEntityBuilder builder){ }
}
