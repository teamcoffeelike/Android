package com.hanul.caramelhomecchiato.util.lifecyclehandler;

import android.content.Context;

import androidx.activity.ComponentActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hanul.caramelhomecchiato.data.Post;
import com.hanul.caramelhomecchiato.network.NetUtils;

import java.util.ArrayList;
import java.util.List;

public class PostScrollHandler extends AbstractScrollHandler<Post>{
	public PostScrollHandler(ComponentActivity activity,
	                         Requester requester,
	                         Listener<Post> listener){
		super(activity, requester, listener);
	}
	public PostScrollHandler(Fragment fragment,
	                         Requester requester,
	                         Listener<Post> listener){
		super(fragment, requester, listener);
	}
	public PostScrollHandler(Context context,
	                         LifecycleOwner lifecycleOwner,
	                         Requester requester,
	                         Listener<Post> listener){
		super(context, lifecycleOwner, requester, listener);
	}

	@Override protected List<Post> toList(JsonObject result){
		List<Post> posts = new ArrayList<>();
		for(JsonElement e : result.get("posts").getAsJsonArray()){
			posts.add(NetUtils.GSON.fromJson(e, Post.class));
		}
		return posts;
	}

	@Override protected long getPostDate(Post post){
		return post.getPostDate();
	}
}
