package com.hanul.caramelhomecchiato.network;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface LoginService{
	LoginService INSTANCE = NetUtils.RETROFIT.create(LoginService.class);

	@FormUrlEncoded
	@POST("loginWithEmail")
	Call<JsonObject> loginWithEmail(@Field("email") String email,
	                                @Field("password") String password);

	@FormUrlEncoded
	@POST("loginWithPhoneNumber")
	Call<JsonObject> loginWithPhoneNumber(@Field("phoneNumber") String phoneNumber,
	                                      @Field("password") String password);

	@FormUrlEncoded
	@POST("loginWithAuthToken")
	Call<JsonObject> loginWithAuthToken(@Field("authToken") String authToken);

	@GET("logout")
	Call<JsonObject> logout();
}
