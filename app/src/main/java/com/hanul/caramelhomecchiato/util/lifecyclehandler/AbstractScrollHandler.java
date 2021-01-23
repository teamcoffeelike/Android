package com.hanul.caramelhomecchiato.util.lifecyclehandler;

import android.content.Context;
import android.util.Log;

import androidx.activity.ComponentActivity;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;

import com.google.gson.JsonObject;
import com.hanul.caramelhomecchiato.util.BaseCallback;

import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Response;

public abstract class AbstractScrollHandler<T> extends LifecycleHandler{
	private static final String TAG = "AbstractScrollHandler";

	private final Requester requester;
	private final Listener<T> listener;

	private State state = State.IDLE;
	@Nullable private Long since = null;
	@Nullable private Call<JsonObject> call;

	public AbstractScrollHandler(ComponentActivity activity,
	                             Requester requester,
	                             Listener<T> listener){
		super(activity);
		this.requester = Objects.requireNonNull(requester);
		this.listener = Objects.requireNonNull(listener);
	}
	public AbstractScrollHandler(Fragment fragment,
	                             Requester requester,
	                             Listener<T> listener){
		super(fragment);
		this.requester = Objects.requireNonNull(requester);
		this.listener = Objects.requireNonNull(listener);
	}
	public AbstractScrollHandler(Context context,
	                             LifecycleOwner lifecycleOwner,
	                             Requester requester,
	                             Listener<T> listener){
		super(context, lifecycleOwner);
		this.requester = Objects.requireNonNull(requester);
		this.listener = Objects.requireNonNull(listener);
	}

	@Override protected void onDestroy(){
		if(call!=null){
			call.cancel();
			call = null;
		}
	}

	public void enqueue(){
		enqueue(false);
	}
	public void enqueue(boolean reset){
		if(!reset&&this.state!=State.IDLE) return;

		if(reset) since = null;
		this.state = reset ? State.AWAITING_RESET_RESPONSE : State.AWAITING_APPEND_RESPONSE;
		if(this.call!=null) this.call.cancel();
		this.call = requester.requestPosts(since);
		this.call.enqueue(new BaseCallback(){
			@Override public void onSuccessfulResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response, @NonNull JsonObject result){
				if(AbstractScrollHandler.this.call!=call) return;

				boolean endOfList = result.get("endOfList").getAsBoolean();
				state = endOfList ? State.END_OF_LIST : State.IDLE;

				List<T> posts = toList(result);
				if(!posts.isEmpty())
					since = getPostDate(posts.get(posts.size()-1));
				AbstractScrollHandler.this.call = null;
				listener.append(posts, endOfList, reset);
			}
			@Override public void onErrorResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response, @NonNull String error){
				if(AbstractScrollHandler.this.call!=call) return;
				Log.e(TAG, "requestPosts: Error: "+error);
				error();
			}
			@Override public void onFailedResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response){
				if(AbstractScrollHandler.this.call!=call) return;
				Log.e(TAG, "requestPosts: Failed: "+response.errorBody());
				error();
			}
			@Override public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t){
				if(AbstractScrollHandler.this.call!=call) return;
				Log.e(TAG, "requestPosts: Unexpected", t);
				error();
			}

			private void error(){
				state = State.ERROR;
				listener.error();
				call = null;
			}
		});
	}

	protected abstract List<T> toList(JsonObject result);
	protected abstract long getPostDate(T t);

	public void reset(){
		since = null;
		state = State.IDLE;
		call = null;
	}

	@Nullable public Long getSince(){
		return since;
	}


	@FunctionalInterface
	public interface Requester{
		Call<JsonObject> requestPosts(@Nullable Long since);
	}

	public interface Listener<T>{
		void append(List<T> list, boolean endOfList, boolean reset);
		void error();
	}

	private enum State{
		IDLE,
		AWAITING_APPEND_RESPONSE,
		AWAITING_RESET_RESPONSE,
		END_OF_LIST,
		ERROR
	}
}
