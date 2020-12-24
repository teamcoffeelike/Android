package com.hanul.caramelhomecchiato.util;

import android.content.Context;

import androidx.annotation.Nullable;

import com.kakao.sdk.auth.LoginClient;
import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.user.UserApiClient;

import java.util.Arrays;

import kotlin.Unit;

public final class KakaoApiUtils{
	private KakaoApiUtils(){}

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

	public static void loginWithNewScopes(Context context, OnKakaoLogin callback, String... scopes){
		LoginClient.getInstance().loginWithNewScopes(context, Arrays.asList(scopes), (oAuthToken, throwable) -> {
			callback.onKakaoLogin(oAuthToken, throwable);
			return Unit.INSTANCE;
		});
	}

	public static void getUser(OnGetKakaoUser callback){
		UserApiClient.getInstance().me((user, throwable) -> {
			callback.onKakaoLogin(user, throwable);
			return Unit.INSTANCE;
		});
	}


	@FunctionalInterface
	public interface OnKakaoLogin {
		void onKakaoLogin(@Nullable OAuthToken token, @Nullable Throwable error);
	}

	@FunctionalInterface
	public interface OnGetKakaoUser{
		void onKakaoLogin(@Nullable com.kakao.sdk.user.model.User user, @Nullable Throwable error);
	}
}
