package com.hanul.caramelhomecchiato.util.lifecyclehandler;

import android.content.Context;

import androidx.activity.ComponentActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hanul.caramelhomecchiato.data.RecipeCover;
import com.hanul.caramelhomecchiato.network.NetUtils;

import java.util.ArrayList;
import java.util.List;

public class RecipeScrollHandler extends AbstractScrollHandler<RecipeCover>{
	public RecipeScrollHandler(ComponentActivity activity, Requester requester, Listener<RecipeCover> listener){
		super(activity, requester, listener);
	}
	public RecipeScrollHandler(Fragment fragment, Requester requester, Listener<RecipeCover> listener){
		super(fragment, requester, listener);
	}
	public RecipeScrollHandler(Context context, LifecycleOwner lifecycleOwner, Requester requester, Listener<RecipeCover> listener){
		super(context, lifecycleOwner, requester, listener);
	}

	@Override protected List<RecipeCover> toList(JsonObject result){
		List<RecipeCover> recipes = new ArrayList<>();
		for(JsonElement e : result.get("recipes").getAsJsonArray()){
			recipes.add(NetUtils.GSON.fromJson(e, RecipeCover.class));
		}
		return recipes;
	}
	@Override protected long getPostDate(RecipeCover recipeCover){
		return recipeCover.getPostDate();
	}
}
