package com.hanul.caramelhomecchiato.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hanul.caramelhomecchiato.data.RecipeTask;
import com.hanul.caramelhomecchiato.data.UserProfile;

public final class NetUtils{
	private NetUtils(){}

	public static final String API_ADDRESS = "192.168.0.21:80/caramelweb/api"; // TODO

	public static final Gson GSON = new GsonBuilder()
			.registerTypeAdapter(RecipeTask.class, RecipeTask.Json.INSTANCE)
			.registerTypeAdapter(UserProfile.class, UserProfile.Json.INSTANCE)
			.setLenient()
			.create();
}
