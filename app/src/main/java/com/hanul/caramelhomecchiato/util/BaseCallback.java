package com.hanul.caramelhomecchiato.util;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class BaseCallback implements Callback<JsonObject>{
	@Override public final void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response){
		if(response.isSuccessful()){
			JsonObject body = response.body();
			if(body!=null){
				if(body.has("error")){
					onErrorResponse(call, response, body.get("error").getAsString());
				}else onSuccessfulResponse(call, response, body);
				return;
			}
		}
		onFailedResponse(call, response);
	}

	public abstract void onSuccessfulResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response, @NonNull JsonObject result);
	public abstract void onErrorResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response, @NonNull String error);
	public abstract void onFailedResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response);
	@Override public abstract void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t);
}
