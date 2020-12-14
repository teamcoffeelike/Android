package com.hanul.caramelhomecchiato.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public final class NetUtils{
	private NetUtils(){}

	public static final String COFFEELIKE = "localhost/coffeelike"; // TODO

	public static final Gson NET_GSON = new GsonBuilder()
			.setLenient()
			.create();
}
