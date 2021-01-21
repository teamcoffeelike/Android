package com.hanul.caramelhomecchiato.network;

import android.net.Uri;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hanul.caramelhomecchiato.BuildConfig;
import com.hanul.caramelhomecchiato.data.RecipeCategory;
import com.hanul.caramelhomecchiato.data.UserProfile;
import com.hanul.caramelhomecchiato.util.UriJsonDeserializer;

import java.net.CookieManager;

import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class NetUtils{
	private NetUtils(){}

	public static final Gson GSON = new GsonBuilder()
			.registerTypeAdapter(UserProfile.class, UserProfile.Json.INSTANCE)
			.registerTypeAdapter(Uri.class, UriJsonDeserializer.INSTANCE)
			.registerTypeAdapter(RecipeCategory.class, RecipeCategory.Json.INSTANCE)
			.setLenient()
			.create();

	public static final OkHttpClient OK_HTTP_CLIENT = new OkHttpClient.Builder()
			.cookieJar(new JavaNetCookieJar(new CookieManager()))
			//.addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
			.build();

	public static final Retrofit RETROFIT = new Retrofit.Builder()
			.client(OK_HTTP_CLIENT)
			.baseUrl("http://"+BuildConfig.API_ADDRESS+"/caramelweb/api/")
			.addConverterFactory(GsonConverterFactory.create())
			.build();
}
