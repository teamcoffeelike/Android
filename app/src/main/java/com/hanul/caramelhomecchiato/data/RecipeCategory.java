package com.hanul.caramelhomecchiato.data;

import androidx.annotation.NonNull;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public enum RecipeCategory{
	HOT_COFFEE,
	ICE_COFFEE,
	TEA,
	ADE,
	SMOOTHIE,
	ETC;

	@NonNull @Override public String toString(){
		return name().toLowerCase();
	}

	public static RecipeCategory fromString(String string){
		return valueOf(string.toUpperCase());
	}


	public enum Json implements JsonDeserializer<RecipeCategory>{
		INSTANCE;

		@Override public RecipeCategory deserialize(JsonElement json,
		                                            Type typeOfT,
		                                            JsonDeserializationContext context) throws JsonParseException{
			return fromString(json.getAsString());
		}
	}
}
