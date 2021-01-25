package com.hanul.caramelhomecchiato.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hanul.caramelhomecchiato.R;
import com.hanul.caramelhomecchiato.data.Post;
import com.hanul.caramelhomecchiato.network.PostService;
import com.hanul.caramelhomecchiato.util.lifecyclehandler.LikedPostScrollHandler;
import com.hanul.caramelhomecchiato.util.lifecyclehandler.PostScrollHandler;

import java.util.List;
import java.util.Objects;

public class LikedPostListFragment extends AbstractPostListFragment{
	private static final String TAG = "LikedPostListFragment";

	public static final String EXTRA_LIKED_BY = "likedBy";

	public static LikedPostListFragment newInstance(int user){
		LikedPostListFragment f = new LikedPostListFragment();
		Bundle args = new Bundle();
		args.putInt(EXTRA_LIKED_BY, user);
		f.setArguments(args);
		return f;
	}

	private TextView textViewEmpty;

	private int likedBy;

	@Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
		Bundle arguments = getArguments();
		likedBy = arguments==null ? 0 : arguments.getInt(EXTRA_LIKED_BY);
		if(likedBy==0) throw new IllegalStateException("LikedPostListFragment에 유저 ID 제공되지 않음");

		View view = Objects.requireNonNull(super.onCreateView(inflater, container, savedInstanceState));

		textViewEmpty = view.findViewById(R.id.textViewEmpty);

		return view;
	}

	@Override protected PostScrollHandler createPostScrollHandler(){
		return new LikedPostScrollHandler(this,
				since -> PostService.INSTANCE.likedPosts(since, 10, likedBy),
				this);
	}

	@Override public void append(List<Post> posts, boolean endOfList, boolean reset){
		super.append(posts, endOfList, reset);
		updateVisibility(postAdapter.elements().isEmpty());
	}

	@Override protected int layout(){
		return R.layout.fragment_liked_post_list;
	}

	private void updateVisibility(boolean empty){
		Log.d(TAG, "updateVisibility: "+empty);
		scrollView.setVisibility(empty ? View.GONE : View.VISIBLE);
		textViewEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
	}
}
