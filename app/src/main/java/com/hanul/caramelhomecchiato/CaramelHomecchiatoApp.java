package com.hanul.caramelhomecchiato;

import android.app.Application;

import com.kakao.sdk.common.KakaoSdk;

public class CaramelHomecchiatoApp extends Application{
	private static final String KAKAO_NATIVE_APP_KEY = "47e844c627448470e1f25b8e1fa0c23e";

	@Override public void onCreate(){
		super.onCreate();
		KakaoSdk.init(this, KAKAO_NATIVE_APP_KEY);
	}


}
