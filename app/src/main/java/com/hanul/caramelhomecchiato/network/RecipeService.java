package com.hanul.caramelhomecchiato.network;

import android.content.ContentResolver;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.gson.JsonObject;
import com.hanul.caramelhomecchiato.data.Recipe;
import com.hanul.caramelhomecchiato.data.RecipeCategory;
import com.hanul.caramelhomecchiato.data.RecipeStep;
import com.hanul.caramelhomecchiato.util.IOUtils;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PartMap;
import retrofit2.http.Query;

public interface RecipeService{
	RecipeService INSTANCE = NetUtils.RETROFIT.create(RecipeService.class);

	@GET("recipeList")
	Call<JsonObject> recipeList(@Query("since") @Nullable Long since,
	                            @Query("pages") @Nullable Integer pages,
	                            @Nullable @Query("category") RecipeCategory category,
	                            @Nullable @Query("author") Integer author);

	@GET("recipe")
	Call<JsonObject> recipe(@Query("id") int id);

	static Future<Call<JsonObject>> writeRecipe(Recipe recipe,
	                                            ExecutorService executorService,
	                                            ContentResolver contentResolver){
		return executorService.submit(() -> {
			Map<String, RequestBody> parts = new HashMap<>();

			List<Future<Entry<String, byte[]>>> holyShitThisLooksBad = new ArrayList<>();

			holyShitThisLooksBad.add(executorService.submit(() ->
					new AbstractMap.SimpleEntry<>("coverImage\"; filename=\"coverImage", IOUtils.read(contentResolver, recipe.getCover().getCoverImage()))));

			for(int i = 0; i<recipe.steps().size(); i++){
				RecipeStep step = recipe.steps().get(i);
				Uri image = step.getImage();
				if(image!=null){
					String name = String.format(Locale.US, "image%1$d\"; filename=\"image%1$d", i+1);
					holyShitThisLooksBad.add(executorService.submit(() ->
							new AbstractMap.SimpleEntry<>(name, IOUtils.read(contentResolver, image))));
				}
			}

			MediaType textPlain = MediaType.parse("text/plain");
			MediaType img = MediaType.parse("image/*");

			parts.put("title", RequestBody.create(recipe.getCover().getTitle(), textPlain));
			parts.put("category", RequestBody.create(recipe.getCover().getCategory().toString(), textPlain));
			parts.put("steps", RequestBody.create(Integer.toString(recipe.steps().size()), textPlain));

			for(int i = 0; i<recipe.steps().size(); i++){
				RecipeStep step = recipe.steps().get(i);
				parts.put("text"+(i+1), RequestBody.create(step.getText(), textPlain));
			}

			for(Future<Entry<String, byte[]>> bad : holyShitThisLooksBad){
				Entry<String, byte[]> e = bad.get();
				parts.put(e.getKey(), RequestBody.create(e.getValue(), img));
			}

			Log.d("G", "writeRecipe: "+parts.keySet());

			return RecipeService.INSTANCE.writeRecipe(parts);
		});
	}

	@Multipart
	@POST("writeRecipe")
	Call<JsonObject> writeRecipe(@PartMap Map<String, RequestBody> parts);

	@Multipart
	@POST("editRecipe")
	Call<JsonObject> editRecipe(@PartMap Map<String, RequestBody> parts);

	@GET("deleteRecipe")
	Call<JsonObject> deleteRecipe(@Query("recipe") int recipe);

	@FormUrlEncoded
	@POST("rateRecipe")
	Call<JsonObject> rateRecipe(@Field("recipe") int recipe, @Field("rating") double rating);

	@FormUrlEncoded
	@POST("deleteRecipeRating")
	Call<JsonObject> deleteRecipeRating(@Field("recipe") int recipe);
}
