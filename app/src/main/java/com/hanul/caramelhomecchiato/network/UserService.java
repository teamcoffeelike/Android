package com.hanul.caramelhomecchiato.network;

import com.google.gson.JsonObject;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface UserService{
	UserService INSTANCE = NetUtils.RETROFIT.create(UserService.class);

	@GET("userSettings")
	Call<JsonObject> userSettings();

	@GET("profile")
	Call<JsonObject> profile(@Query("userId") int userId);

	@FormUrlEncoded
	@POST("setName")
	Call<JsonObject> setName(@Field("name") String name);

	@FormUrlEncoded
	@POST("setMotd")
	Call<JsonObject> setMotd(@Field("motd") String motd);

	static Call<JsonObject> setProfileImage(byte[] profileImage){
		return INSTANCE.setProfileImage(
				MultipartBody.Part.createFormData("profileImage", "profileImage",
						RequestBody.create(profileImage, MediaType.parse("image/*")))
		);
	}

	@Multipart
	@POST("setProfileImage")
	Call<JsonObject> setProfileImage(@Part MultipartBody.Part profileImage);

	@FormUrlEncoded
	@POST("setPassword")
	Call<JsonObject> setPassword(@Field("password") String password,
	                             @Field("newPassword") String newPassword);

	@GET("getFollower")
	Call<JsonObject> getFollower(@Query("user") int user);

	@GET("getFollowing")
	Call<JsonObject> getFollowing(@Query("user") int user);

	@FormUrlEncoded
	@POST("setFollowing")
	Call<JsonObject> setFollowing(@Field("followingId") int followingId, @Field("following") boolean following);

	@FormUrlEncoded
	@POST("searchUserByName")
	Call<JsonObject> searchUserByName(@Field("name") String name);
}
