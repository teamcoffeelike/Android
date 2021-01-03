package com.hanul.caramelhomecchiato.network;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface UserService{
	UserService INSTANCE = NetUtils.RETROFIT.create(UserService.class);

	@GET("userSettings")
	Call<JsonObject> userSettings();

	@GET("profile")
	Call<JsonObject> profile(@Query("userId") int userId);

	@POST("setName")
	Call<JsonObject> setName(@Field("name") String name);

	@POST("setPassword")
	Call<JsonObject> setPassword(@Field("password") String password,
	                             @Field("newPassword") String newPassword);

	@GET("getFollower")
	Call<JsonObject> getFollower(@Query("user") int user);

	@GET("getFollowing")
	Call<JsonObject> getFollowing(@Query("user") int user);

	@POST("setFollowing")
	Call<JsonObject> setFollowing(@Field("followingId") int followingId, @Field("following") boolean following);
}
