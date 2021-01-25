package com.hanul.caramelhomecchiato.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.hanul.caramelhomecchiato.R;
import com.hanul.caramelhomecchiato.fragment.LikedPostListFragment;

public class LikesActivity extends AppCompatActivity{
	public static final String EXTRA_USER = "user";

	private LikedPostListFragment fragment;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_likes);

		int user = getIntent().getIntExtra(EXTRA_USER, 0);
		if(user==0) throw new IllegalStateException("LikesActivity에 User 제공되지 않음");

		fragment = LikedPostListFragment.newInstance(user);

		getSupportFragmentManager()
				.beginTransaction()
				.replace(R.id.frag, fragment)
				.commit();
	}
}