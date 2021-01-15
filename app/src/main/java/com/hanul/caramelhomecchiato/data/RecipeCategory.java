package com.hanul.caramelhomecchiato.data;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.hanul.caramelhomecchiato.R;

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

	@ColorRes public int getColor(){
		switch(this){
			case HOT_COFFEE: return R.color.camel;
			case ICE_COFFEE: return R.color.CadetBlueCrayola;
			case TEA: return R.color.Asparagus;
			case ADE: return R.color.OrangeYellowCrayola;
			case SMOOTHIE: return R.color.NewYorkPink;
			case ETC: return R.color.ShinyShamrock;
			default: throw new IllegalStateException("Unreachable");
		}
	}

	@DrawableRes public int getIcon(){
		switch(this){
			case HOT_COFFEE: return R.drawable.hot_coffee;
			case ICE_COFFEE: return R.drawable.ice_coffee;
			case TEA: return R.drawable.tea;
			case ADE: return R.drawable.ade;
			case SMOOTHIE: return R.drawable.smoothie;
			case ETC: return R.drawable.etc;
			default: throw new IllegalStateException("Unreachable");
		}
	}

	@StringRes public int getName(){
		switch(this){
			case HOT_COFFEE: return R.string.recipe_category_hot_coffee;
			case ICE_COFFEE: return R.string.recipe_category_ice_coffee;
			case TEA: return R.string.recipe_category_tea;
			case ADE: return R.string.recipe_category_ade;
			case SMOOTHIE: return R.string.recipe_category_smoothie;
			case ETC: return R.string.recipe_category_etc;
			default: throw new IllegalStateException("Unreachable");
		}
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
