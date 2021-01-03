package com.hanul.caramelhomecchiato.util;

import android.app.Application;
import android.content.Context;

import androidx.annotation.Nullable;

import com.google.gson.JsonObject;

// TODO 로그아웃 시 재로그인?
public final class Auth{
	private static Auth authCache;

	/**
	 * INTERNAL ONLY DON'T EVER CALL IT
	 */
	public static void init(Application application){
		if(authCache!=null) throw new IllegalStateException("Initializing singleton twice");
		authCache = new Auth(application);
	}

	public static Auth getInstance(){
		return authCache;
	}

	private final Application application;
	@Nullable private final Lazy<String> authToken = new Lazy<>(ctx ->
			ctx.getSharedPreferences("auth", Context.MODE_PRIVATE)
					.getString("token", null));
	@Nullable private Integer loginUser;

	private Auth(Application application){
		this.application = application;
	}

	/**
	 * 로그인 토큰을 반환합니다.
	 */
	@Nullable public String getAuthToken(){
		return authToken.get();
	}

	public void setAuthToken(@Nullable String authToken){
		this.authToken.set(authToken);
		if(authToken!=null){
			application.getApplicationContext()
					.getSharedPreferences("auth", Context.MODE_PRIVATE)
					.edit()
					.putString("token", authToken)
					.apply();
		}else{
			application.getApplicationContext()
					.getSharedPreferences("auth", Context.MODE_PRIVATE)
					.edit()
					.remove("token")
					.apply();
		}
	}

	@Nullable public Integer getLoginUser(){
		return loginUser;
	}
	public void setLoginUser(@Nullable Integer loginUser){
		this.loginUser = loginUser;
	}

	public void setLoginData(JsonObject jsonObject){
		setAuthToken(jsonObject.get("authToken").getAsString());
		setLoginUser(jsonObject.get("userId").getAsInt());
	}

	public void removeLoginData(){
		setAuthToken(null);
		setLoginUser(null);
	}


	private final class Lazy<T>{
		private final Initializer<T> initializer;
		private T t;
		private boolean initialized = false;

		private Lazy(Initializer<T> initializer){
			this.initializer = initializer;
		}

		public T get(){
			if(!initialized){
				synchronized(this){
					if(!initialized){
						initialized = true;
						t = initializer.initialize(application.getApplicationContext());
					}
				}
			}
			return t;
		}

		public void set(T t){
			if(!initialized){
				synchronized(this){
					if(!initialized){
						initialized = true;
						this.t = t;
					}
				}
			}else{
				this.t = t;
			}
		}
	}

	@FunctionalInterface
	private interface Initializer<T>{
		T initialize(Context ctx);
	}
}
