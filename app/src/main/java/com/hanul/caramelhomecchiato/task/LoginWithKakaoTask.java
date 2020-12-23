package com.hanul.caramelhomecchiato.task;

import com.kakao.sdk.auth.model.OAuthToken;

import org.apache.http.entity.mime.MultipartEntityBuilder;

public class LoginWithKakaoTask<CONTEXT> extends JsonResponseTask<CONTEXT>{
	private final OAuthToken oAuthToken;

	public LoginWithKakaoTask(CONTEXT context, OAuthToken oAuthToken){
		super(context, "loginWithKakao");
		this.oAuthToken = oAuthToken;
	}

	@Override protected void appendMultipartEntity(MultipartEntityBuilder builder){
		builder.addTextBody("kakaoLoginToken", oAuthToken.getAccessToken());
	}
}
