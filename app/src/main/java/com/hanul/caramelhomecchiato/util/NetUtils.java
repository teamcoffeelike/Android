package com.hanul.caramelhomecchiato.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public final class NetUtils{
	private NetUtils(){}

	public static final String API_ADDRESS = "192.168.0.21:80/coffeelike"; // TODO

	public static final Gson GSON = new GsonBuilder()
			.setLenient()
			.create();
}
