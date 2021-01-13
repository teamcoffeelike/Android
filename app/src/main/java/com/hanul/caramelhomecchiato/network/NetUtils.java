package com.hanul.caramelhomecchiato.network;

import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hanul.caramelhomecchiato.BuildConfig;
import com.hanul.caramelhomecchiato.data.RecipeTask;
import com.hanul.caramelhomecchiato.data.UserProfile;
import com.hanul.caramelhomecchiato.util.UriJsonDeserializer;

import java.io.IOException;
import java.net.CookieManager;

import okhttp3.Interceptor;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class NetUtils{
	private static final String TAG = "Network";

	private NetUtils(){}

	public static final Gson GSON = new GsonBuilder()
			.registerTypeAdapter(RecipeTask.class, RecipeTask.Json.INSTANCE)
			.registerTypeAdapter(UserProfile.class, UserProfile.Json.INSTANCE)
			.registerTypeAdapter(Uri.class, UriJsonDeserializer.INSTANCE)
			.setLenient()
			.create();

	public static final OkHttpClient OK_HTTP_CLIENT = new OkHttpClient.Builder()
			.cookieJar(new JavaNetCookieJar(new CookieManager()))
			//.addNetworkInterceptor(RequestLogger.INSTANCE)
			.build();

	public static final Retrofit RETROFIT = new Retrofit.Builder()
			.client(OK_HTTP_CLIENT)
			.baseUrl("http://"+BuildConfig.API_ADDRESS+"/caramelweb/api/")
			.addConverterFactory(GsonConverterFactory.create())
			.build();

	private enum RequestLogger implements Interceptor{
		INSTANCE;

		@Override public Response intercept(Chain chain) throws IOException{
			Request request = chain.request();
			Log.d(TAG, "Sending request to "+request.url()+" on "+chain.connection()+"\n"+request.headers());

			long t1 = System.currentTimeMillis();

			Response response = chain.proceed(request);

			long t2 = System.currentTimeMillis();

			Log.d(TAG, "Received response for "+response.request().url()+" in "+((t2-t1))+"ms\n"+response.headers());
			return response;
		}
	}
}
