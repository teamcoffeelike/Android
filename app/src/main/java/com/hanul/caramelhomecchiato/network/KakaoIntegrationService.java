package com.hanul.caramelhomecchiato.network;

import androidx.annotation.Nullable;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface KakaoIntegrationService{
	KakaoIntegrationService INSTANCE = NetUtils.RETROFIT.create(KakaoIntegrationService.class);

	@FormUrlEncoded
	@POST("loginWithKakao")
	Call<JsonObject> loginWithKakao(@Field("kakaoLoginToken") String kakaoLoginToken);

	@FormUrlEncoded
	@POST("joinWithKakao")
	Call<JsonObject> joinWithKakao(@Field("kakaoLoginToken") String kakaoLoginToken,
	                               @Nullable @Field("name") String name);
}
