package com.hanul.caramelhomecchiato.adapter;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;
import com.hanul.caramelhomecchiato.data.Post;
import com.hanul.caramelhomecchiato.event.PostDeleteEvent;
import com.hanul.caramelhomecchiato.event.PostEditEvent;
import com.hanul.caramelhomecchiato.event.Ticket;
import com.hanul.caramelhomecchiato.network.NetUtils;
import com.hanul.caramelhomecchiato.network.PostService;
import com.hanul.caramelhomecchiato.util.BaseCallback;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public abstract class AbstractPostAdapter extends BaseAdapter<Post>{
	private static final String TAG = "AbstractPostAdapter";

	@SuppressWarnings("unused") private final Ticket postDeleteEventTicket = PostDeleteEvent.subscribeAll(this::onPostDeleted);
	@SuppressWarnings("unused") private final Ticket postEditEventTicket = PostEditEvent.subscribeAll(this::onPostEdited);

	protected void onPostDeleted(int postId){
		List<Post> elements = elements();
		for(int i = 0; i<elements.size(); i++){
			Post post = elements.get(i);
			if(post.getId()==postId){
				elements.remove(i);
				notifyItemRemoved(i);
				return;
			}
		}
	}

	protected void onPostEdited(int postId){
		List<Post> elements = elements();
		for(int i = 0; i<elements.size(); i++){
			Post post = elements.get(i);
			if(post.getId()==postId){
				Log.d(TAG, "onPostEdited: Enqueueing post call");
				PostService.INSTANCE.post(postId).enqueue(new BaseCallback(){
					@Override public void onSuccessfulResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response, @NonNull JsonObject result){
						onPostFetchSucceed(NetUtils.GSON.fromJson(result, Post.class));
					}
					@Override public void onErrorResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response, @NonNull String error){
						Log.e(TAG, "onErrorResponse: "+error);
						onPostFetchFailed();
					}
					@Override public void onFailedResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response){
						Log.e(TAG, "onErrorResponse: "+response.errorBody());
						onPostFetchFailed();
					}
					@Override public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t){
						Log.e(TAG, "onErrorResponse: ", t);
						onPostFetchFailed();
					}
				});
				notifyItemChanged(i);
				return;
			}
		}
	}

	protected void onPostFetchSucceed(Post post){
		List<Post> elements = elements();
		for(int i = 0; i<elements.size(); i++){
			Post p2 = elements.get(i);
			if(p2.getId()==post.getId()){
				Log.d(TAG, "onPostFetchSucceed: Applying change");
				elements.set(i, post);
				notifyItemChanged(i);
				return;
			}
		}
	}

	protected void onPostFetchFailed(){}
}
