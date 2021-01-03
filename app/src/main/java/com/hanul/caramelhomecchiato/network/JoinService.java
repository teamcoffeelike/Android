package com.hanul.caramelhomecchiato.network;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface JoinService{
	JoinService INSTANCE = NetUtils.RETROFIT.create(JoinService.class);

	@FormUrlEncoded
	@POST("joinWithEmail")
	Call<JsonObject> joinWithEmail(@Field("name") String name,
	                               @Field("email") String email,
	                               @Field("password") String password);

	@FormUrlEncoded
	@POST("joinWithPhoneNumber")
	Call<JsonObject> joinWithPhoneNumber(@Field("name") String name,
	                                     @Field("phoneNumber") String phoneNumber,
	                                     @Field("password") String password);
}
