package com.hanul.caramelhomecchiato.network;

import androidx.annotation.Nullable;

import com.google.gson.JsonObject;
import com.hanul.caramelhomecchiato.data.RecipeCategory;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface RecipeService{
	RecipeService INSTANCE = NetUtils.RETROFIT.create(RecipeService.class);

	@GET("recipeList")
	Call<JsonObject> recipeList(@Query("since") @Nullable Long since, @Query("pages") @Nullable Integer pages, @Query("category") RecipeCategory category);

	@GET("recipeList")
	Call<JsonObject> recipeList(@Query("since") @Nullable Long since, @Query("pages") @Nullable Integer pages, @Query("author") int author);

	@GET("recipe")
	Call<JsonObject> recipe(@Query("id") int id);

	@GET("deleteRecipe")
	Call<JsonObject> deleteRecipe(@Query("recipe") int recipe);

	@POST("deleteRecipe")
	Call<JsonObject> rateRecipe(@Query("recipe") int recipe, @Query("rating") double rating);
}
