package com.hanul.caramelhomecchiato.util.lifecyclehandler;

import android.content.Context;

import androidx.activity.ComponentActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;

import com.hanul.caramelhomecchiato.data.Post;

import java.util.Objects;

public class LikedPostScrollHandler extends PostScrollHandler{
	public LikedPostScrollHandler(ComponentActivity activity, Requester requester, Listener<Post> listener){
		super(activity, requester, listener);
	}
	public LikedPostScrollHandler(Fragment fragment, Requester requester, Listener<Post> listener){
		super(fragment, requester, listener);
	}
	public LikedPostScrollHandler(Context context, LifecycleOwner lifecycleOwner, Requester requester, Listener<Post> listener){
		super(context, lifecycleOwner, requester, listener);
	}

	@Override protected long getPostDate(Post post){
		return Objects.requireNonNull(post.getLikedDate());
	}
}
