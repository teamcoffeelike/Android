package com.hanul.caramelhomecchiato.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonElement;
import com.hanul.caramelhomecchiato.R;
import com.hanul.caramelhomecchiato.adapter.PostAdapter;
import com.hanul.caramelhomecchiato.data.Post;
import com.hanul.caramelhomecchiato.task.GetRecentPostTask;
import com.hanul.caramelhomecchiato.util.NetUtils;

import java.util.ArrayList;
import java.util.List;

public class RecentPostFragment extends Fragment{
	private static final String TAG = "RecentPostFragment";

	@Nullable private List<Post> recentPosts;
	private PostAdapter postAdapter;

	@Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.fragment_popular_post, container, false);

		Context context = getContext();

		Log.d(TAG, "onCreateView: "+context);

		if(context!=null){
			RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
			recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));

			postAdapter = new PostAdapter();

			if(recentPosts!=null) postAdapter.elements().addAll(recentPosts);

			recyclerView.setAdapter(postAdapter);
		}
		return view;
	}

	@Override public void onResume(){
		super.onResume();
		Log.d(TAG, "onResume: Pog");
		new GetRecentPostTask<>(this)
				.onSucceed((frag, o) -> {
					if(o.has("error")){
						String error = o.get("error").getAsString();
						Log.e(TAG, "postLoad: GetProfileTask 오류: "+error);
						return;
					}
					List<Post> posts = new ArrayList<>();
					for(JsonElement e : o.get("posts").getAsJsonArray()){
						posts.add(NetUtils.GSON.fromJson(e, Post.class));
					}
					frag.setRecentPosts(posts);
				})
				.onCancelled((a2, e2) -> {
					Log.e(TAG, "postLoad: GetRecentPostTask 오류: "+e2);
				}).execute();
	}


	@Nullable public List<Post> getRecentPosts(){
		return recentPosts;
	}
	public void setRecentPosts(@Nullable List<Post> recentPosts){
		this.recentPosts = recentPosts;
		if(postAdapter!=null){
			List<Post> elements = postAdapter.elements();
			elements.clear();
			if(recentPosts!=null) elements.addAll(recentPosts);
			postAdapter.notifyDataSetChanged();
		}
	}
}
