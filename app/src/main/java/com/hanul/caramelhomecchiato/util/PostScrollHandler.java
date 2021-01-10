package com.hanul.caramelhomecchiato.util;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;
import com.hanul.caramelhomecchiato.data.Post;

import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Response;

public class PostScrollHandler extends AutoDisposed{
	private static final String TAG = "PostScrollHandler";

	private final RecyclerView recyclerView;
	private final Requester requester;
	private final Listener listener;

	private final RecyclerView.OnScrollListener onScrollListener;

	private State state = State.AWAITING_ACTION;
	@Nullable private Long since = null;

	public PostScrollHandler(RecyclerView recyclerView,
	                         LifecycleOwner lifecycleOwner,
	                         Requester requester,
	                         Listener listener){
		super(recyclerView.getContext(), lifecycleOwner);
		this.recyclerView = recyclerView;
		this.requester = Objects.requireNonNull(requester);
		this.listener = Objects.requireNonNull(listener);

		onScrollListener = new RecyclerView.OnScrollListener(){
			@Override public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy){
				Log.d(TAG, "onScrolled: "+recyclerView.getMeasuredHeight()+" "+dy);
				if(dy<recyclerView.getMeasuredHeight()-20){
					Log.d(TAG, "onScrolled: ok?");
					state = State.AWAITING_RESPONSE;
					requester.requestPosts(since).enqueue(new BaseCallback(){
						@Override public void onSuccessfulResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response, @NonNull JsonObject result){
							// TODO 무한스크롤
						}
						@Override public void onErrorResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response, @NonNull String error){
							Log.e(TAG, "requestPosts: Error: "+error);
							state = State.ERROR;
						}
						@Override public void onFailedResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response){
							Log.e(TAG, "requestPosts: Failed: "+response.errorBody());
							state = State.ERROR;
						}
						@Override public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t){
							Log.e(TAG, "requestPosts: Unexpected", t);
							state = State.ERROR;
						}
					});
				}
			}
		};
		recyclerView.addOnScrollListener(onScrollListener);
	}

	@Override protected void onDestroy(){
		recyclerView.removeOnScrollListener(onScrollListener);
	}

	public interface Requester{
		Call<JsonObject> requestPosts(@Nullable Long since);
	}

	public interface Listener{
		void append(List<Post> posts, boolean endOfList);
		void error();
	}

	private enum State{
		AWAITING_ACTION,
		AWAITING_RESPONSE,
		END_OF_LIST,
		ERROR
	}
}
