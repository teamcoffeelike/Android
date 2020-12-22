package com.hanul.caramelhomecchiato.util;

import android.content.Context;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kakao.sdk.auth.LoginClient;
import com.kakao.sdk.auth.model.OAuthToken;

import kotlin.Unit;

public final class NetUtils{
	private NetUtils(){}

	public static final String API_ADDRESS = "192.168.0.21:80/coffeelike"; // TODO

	public static final String SUCCESS = "success";

	public static final Gson GSON = new GsonBuilder()
			.setLenient()
			.create();

	public static void loginWithKakao(Context context, OnKakaoLogin callback){
		if(LoginClient.getInstance().isKakaoTalkLoginAvailable(context)){
			LoginClient.getInstance().loginWithKakaoTalk(context, (oAuthToken, error) -> {
				callback.onKakaoLogin(oAuthToken, error);
				return Unit.INSTANCE; // ㅋㅋ
			});
		}else{
			LoginClient.getInstance().loginWithKakaoAccount(context, (oAuthToken, error) -> {
				callback.onKakaoLogin(oAuthToken, error);
				return Unit.INSTANCE; // ㅋㅋ
			});
		}
	}

	public interface OnKakaoLogin {
		void onKakaoLogin(@Nullable OAuthToken token, @Nullable Throwable error);
	}
}
