package com.hanul.caramelhomecchiato.task;

import org.apache.http.entity.mime.MultipartEntityBuilder;

public class GetRecentPostTask<CONTEXT> extends JsonResponseTask<CONTEXT>{
	public GetRecentPostTask(CONTEXT context){
		super(context, "recentPosts");
	}

	@Override protected void appendMultipartEntity(MultipartEntityBuilder builder){}
}
