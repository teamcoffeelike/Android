package com.hanul.caramelhomecchiato.util;

import android.content.Context;

import androidx.annotation.Nullable;

public final class AuthUtils{
	private AuthUtils(){}

	/**
	 * 로그인 토큰을 반환합니다.
	 */
	@Nullable public static String getAuthToken(Context context){
		return ""; // TODO implementation
	}

	/**
	 * 로그인 토큰 발급 토큰을 반환합니다.
	 */
	@Nullable public static String getRefreshToken(Context context){
		return ""; // TODO implementation
	}

	public static void setAuthToken(Context context, @Nullable String authToken, @Nullable String refreshToken){
		// TODO implementation
	}
}
