package com.hanul.caramelhomecchiato;

import android.app.Application;

import com.hanul.caramelhomecchiato.util.Auth;
import com.hanul.caramelhomecchiato.util.GlideUtils;
import com.kakao.sdk.common.KakaoSdk;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CaramelHomecchiatoApp extends Application{
	public final ExecutorService executorService = Executors.newFixedThreadPool(5);

	private static final String KAKAO_NATIVE_APP_KEY = "47e844c627448470e1f25b8e1fa0c23e";

	@Override public void onCreate(){
		super.onCreate();
		KakaoSdk.init(this, KAKAO_NATIVE_APP_KEY);
		Auth.init(this);
		GlideUtils.init(this);
	}
}
