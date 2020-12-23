package com.hanul.caramelhomecchiato.task;

import androidx.annotation.Nullable;

import com.kakao.sdk.auth.model.OAuthToken;

import org.apache.http.entity.mime.MultipartEntityBuilder;

public class JoinWithKakaoTask<CONTEXT> extends JsonResponseTask<CONTEXT>{
	private final OAuthToken token;
	@Nullable private final String name;

	public JoinWithKakaoTask(CONTEXT context, OAuthToken token, @Nullable String name){
		super(context, "joinWithKakao");
		this.token = token;
		this.name = name;
	}

	@Override protected void appendMultipartEntity(MultipartEntityBuilder builder){
		builder.addTextBody("token", token.getAccessToken());
		if(name!=null) builder.addTextBody("name", name);
	}
}
