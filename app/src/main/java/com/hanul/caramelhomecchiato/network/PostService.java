package com.hanul.caramelhomecchiato.network;

import androidx.annotation.Nullable;

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

public interface PostService{
	PostService INSTANCE = NetUtils.RETROFIT.create(PostService.class);

	@GET("recentPosts")
	Call<JsonObject> recentPosts(@Query("since") @Nullable Long since, @Query("pages") @Nullable Integer pages);

	@GET("usersPosts")
	Call<JsonObject> usersPosts(@Query("since") @Nullable Long since, @Query("pages") @Nullable Integer pages, @Query("id") int id);

	@GET("likedPosts")
	Call<JsonObject> likedPosts(@Query("since") @Nullable Long since, @Query("pages") @Nullable Integer pages, @Query("likedBy") int likedBy);

	@GET("post")
	Call<JsonObject> post(@Query("id") int id);

	/*
	 * static으로 안 하면 retrofit이 지랄발광함. https://github.com/square/retrofit/issues/1768
	 */
	static Call<JsonObject> writePost(String text, byte[] image){
		return INSTANCE.writePost(
				MultipartBody.Part.createFormData("text", text),
				MultipartBody.Part.createFormData("image", "image",
						RequestBody.create(image, MediaType.parse("image/*")))
		);
	}

	@Multipart
	@POST("writePost")
	Call<JsonObject> writePost(@Part MultipartBody.Part text, @Part MultipartBody.Part image);

	@FormUrlEncoded
	@POST("editPost")
	Call<JsonObject> editPost(@Field("post") int post, @Field("text") String text);

	static Call<JsonObject> editPostImage(int post, byte[] image){
		return INSTANCE.editPostImage(
				MultipartBody.Part.createFormData("post", Integer.toString(post)),
				MultipartBody.Part.createFormData("image", "image",
						RequestBody.create(image, MediaType.parse("image/*")))
		);
	}

	@Multipart
	@POST("editPostImage")
	Call<JsonObject> editPostImage(@Part MultipartBody.Part post, @Part MultipartBody.Part image);

	@FormUrlEncoded
	@POST("deletePost")
	Call<JsonObject> deletePost(@Field("post") int post);

	@FormUrlEncoded
	@POST("likePost")
	Call<JsonObject> likePost(@Field("post") int post, @Field("like") boolean like);
}
