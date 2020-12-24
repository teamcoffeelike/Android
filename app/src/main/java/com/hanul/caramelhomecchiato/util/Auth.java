package com.hanul.caramelhomecchiato.util;

import android.content.Context;

import androidx.annotation.Nullable;

public final class Auth{
	@Nullable private String authToken;

	private Auth(){}

	@Nullable private static Auth authCache;

	/**
	 * 로그인 토큰을 반환합니다.
	 */
	@Nullable public static String getAuthToken(Context context){
		if(authCache==null){
			synchronized(Auth.class){
				if(authCache==null){
					authCache = new Auth();
					authCache.authToken = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
							.getString("token", null);
				}
			}
		}
		return authCache.authToken;
	}

	public static void setAuthToken(Context context, @Nullable String authToken){
		if(authCache==null){
			synchronized(Auth.class){
				if(authCache==null){
					authCache = new Auth();
				}
			}
		}
		authCache.authToken = authToken;
		context.getSharedPreferences("auth", Context.MODE_PRIVATE)
				.edit()
				.remove("token").apply();
	}
}
