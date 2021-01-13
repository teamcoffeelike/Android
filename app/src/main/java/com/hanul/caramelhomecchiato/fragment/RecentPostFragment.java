package com.hanul.caramelhomecchiato.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hanul.caramelhomecchiato.R;
import com.hanul.caramelhomecchiato.adapter.PostAdapter;
import com.hanul.caramelhomecchiato.data.Post;
import com.hanul.caramelhomecchiato.network.NetUtils;
import com.hanul.caramelhomecchiato.network.PostService;
import com.hanul.caramelhomecchiato.util.BaseCallback;
import com.hanul.caramelhomecchiato.util.FollowingEventHandler;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class RecentPostFragment extends Fragment{
	private static final String TAG = "RecentPostFragment";

	@Nullable private List<Post> recentPosts;
	private PostAdapter postAdapter;

	@Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.fragment_popular_post, container, false);

		Context context = getContext();

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

		PostService.INSTANCE.recentPosts().enqueue(new BaseCallback(){
			@Override public void onSuccessfulResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response, @NonNull JsonObject result){
				List<Post> posts = new ArrayList<>();
				for(JsonElement e : result.get("posts").getAsJsonArray()){
					Post post = NetUtils.GSON.fromJson(e, Post.class);
					FollowingEventHandler.dispatch(post.getAuthor());
					posts.add(post);
				}
				setRecentPosts(posts);
			}
			@Override public void onErrorResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response, @NonNull String error){
				Log.e(TAG, "recentPosts: Error "+error);
				toastError();
			}
			@Override public void onFailedResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response){
				Log.e(TAG, "recentPosts: Failure "+response.errorBody());
				toastError();
			}
			@Override public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t){
				Log.e(TAG, "recentPosts: Failure", t);
				toastError();
			}

			private void toastError(){
				Toast.makeText(getContext(), "예상치 못한 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override public void onStop(){
		super.onStop();
		postAdapter.clearEvents();
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
