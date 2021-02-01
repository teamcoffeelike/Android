package com.hanul.caramelhomecchiato.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;
import com.hanul.caramelhomecchiato.R;
import com.hanul.caramelhomecchiato.data.Post;
import com.hanul.caramelhomecchiato.event.FollowingEvent;
import com.hanul.caramelhomecchiato.event.PostDeleteEvent;
import com.hanul.caramelhomecchiato.event.PostLikeEvent;
import com.hanul.caramelhomecchiato.event.Ticket;
import com.hanul.caramelhomecchiato.network.NetUtils;
import com.hanul.caramelhomecchiato.network.PostService;
import com.hanul.caramelhomecchiato.util.BaseCallback;
import com.hanul.caramelhomecchiato.util.lifecyclehandler.PostViewHandler;

import retrofit2.Call;
import retrofit2.Response;

public class PostActivity extends AppCompatActivity{
	private static final String TAG = "PostActivity";

	public static final String EXTRA_POST_ID = "postId";

	private PostViewHandler postViewHandler;

	private int postId;

	@Nullable private Ticket postDeleteEventTicket;

	@Override protected void onCreate(@Nullable Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_post);

		postId = getIntent().getIntExtra(EXTRA_POST_ID, 0);
		if(postId==0) throw new IllegalStateException("PostActivity에 Post가 제공되지 않음.");

		this.postViewHandler = new PostViewHandler(this, true);

		if(postDeleteEventTicket!=null) postDeleteEventTicket.unsubscribe();
		postDeleteEventTicket = PostDeleteEvent.subscribe(postId, postId -> finish());
	}

	@Override protected void onResume(){
		super.onResume();
		PostService.INSTANCE.post(postId).enqueue(new BaseCallback(){
			@Override public void onSuccessfulResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response, @NonNull JsonObject result){
				Post post = NetUtils.GSON.fromJson(result, Post.class);
				postViewHandler.setPost(post);
				FollowingEvent.dispatch(post.getAuthor());
				PostLikeEvent.dispatch(post);
			}
			@Override public void onErrorResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response, @NonNull String error){
				if("no_post".equals(error)){
					Toast.makeText(PostActivity.this, "존재하지 않는 포스트입니다.", Toast.LENGTH_SHORT).show();
				}else{
					Log.e(TAG, "onErrorResponse: "+error);
					Toast.makeText(PostActivity.this, "포스트를 불러오는 도중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
				}
				finish();
			}
			@Override public void onFailedResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response){
				Log.e(TAG, "onErrorResponse: "+response.errorBody());
				Toast.makeText(PostActivity.this, "포스트를 불러오는 도중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
				finish();
			}
			@Override public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t){
				Log.e(TAG, "onErrorResponse: ", t);
				Toast.makeText(PostActivity.this, "포스트를 불러오는 도중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
				finish();
			}
		});
	}
}
