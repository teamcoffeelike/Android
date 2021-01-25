package com.hanul.caramelhomecchiato.fragment;

import com.hanul.caramelhomecchiato.R;
import com.hanul.caramelhomecchiato.network.PostService;
import com.hanul.caramelhomecchiato.util.lifecyclehandler.PostScrollHandler;

public class PostListFragment extends AbstractPostListFragment{
	@Override protected int layout(){
		return R.layout.fragment_post_list;
	}
	@Override protected PostScrollHandler createPostScrollHandler(){
		return new PostScrollHandler(this,
				since -> PostService.INSTANCE.recentPosts(since, 10),
				this);
	}
}
