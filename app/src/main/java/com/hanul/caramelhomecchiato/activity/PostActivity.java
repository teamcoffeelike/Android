package com.hanul.caramelhomecchiato.activity;

import android.os.Bundle;
import android.os.Parcelable;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.hanul.caramelhomecchiato.R;
import com.hanul.caramelhomecchiato.data.Post;
import com.hanul.caramelhomecchiato.util.lifecyclehandler.PostViewHandler;

public class PostActivity extends AppCompatActivity{
	public static final String EXTRA_POST = "post";

	private PostViewHandler postViewHandler;

	@Override protected void onCreate(@Nullable Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_post);

		Parcelable postExtra = getIntent().getParcelableExtra(EXTRA_POST);
		if(!(postExtra instanceof Post)){
			throw new IllegalStateException("PostActivity에 Post가 제공되지 않음.");
		}

		Post post = (Post)postExtra;

		this.postViewHandler = new PostViewHandler(this);
		this.postViewHandler.setPost(post);
	}

	@Override protected void onResume(){
		super.onResume();
		postViewHandler.subscribeFollowEvent();
	}

	@Override protected void onPause(){
		super.onPause();
		postViewHandler.unsubscribeFollowEvent();
	}
}
