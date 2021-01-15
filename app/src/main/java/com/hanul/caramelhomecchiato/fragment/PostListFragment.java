package com.hanul.caramelhomecchiato.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hanul.caramelhomecchiato.R;
import com.hanul.caramelhomecchiato.adapter.PostAdapter;
import com.hanul.caramelhomecchiato.data.Post;
import com.hanul.caramelhomecchiato.event.FollowingEventDispatcher;
import com.hanul.caramelhomecchiato.network.PostService;
import com.hanul.caramelhomecchiato.util.lifecyclehandler.PostScrollHandler;

import java.util.List;

public class PostListFragment extends Fragment implements PostScrollHandler.Listener<Post>{
	private PostAdapter postAdapter;
	private TextView textViewEndOfList;

	private final PostScrollHandler postScrollHandler = new PostScrollHandler(this,
			since -> PostService.INSTANCE.recentPosts(since, 10),
			this);

	@Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.fragment_post_list, container, false);

		Context context = getContext();
		if(context==null) throw new IllegalStateException("RecentPostFragment에 context 없음");

		textViewEndOfList = view.findViewById(R.id.textViewEndOfList);

		RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
		recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));

		postAdapter = new PostAdapter();
		recyclerView.setAdapter(postAdapter);

		NestedScrollView scrollView = view.findViewById(R.id.scrollView);
		scrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener)(v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
			int bottom = v.getChildAt(0).getBottom();
			int y = v.getHeight()+scrollY;

			if(bottom-2000<=y){
				postScrollHandler.enqueue();
			}
		});

		return view;
	}

	@Override public void onResume(){
		super.onResume();
		postScrollHandler.enqueue(postAdapter.elements().isEmpty());
	}

	@Override public void append(List<Post> posts, boolean endOfList, boolean reset){
		for(Post post : posts){
			FollowingEventDispatcher.dispatch(post.getAuthor());
		}
		List<Post> elements = postAdapter.elements();
		int size = elements.size();
		if(reset){
			elements.clear();
			postAdapter.notifyItemRangeRemoved(0, size);
			elements.addAll(posts);
			postAdapter.notifyItemRangeInserted(0, posts.size());
		}else{
			elements.addAll(posts);
			postAdapter.notifyItemRangeInserted(size, posts.size());
		}
		if(endOfList){
			textViewEndOfList.setVisibility(View.VISIBLE);
			textViewEndOfList.setText(R.string.post_list_end);
		}else{
			textViewEndOfList.setVisibility(View.GONE);
		}
	}

	@Override public void error(){
		textViewEndOfList.setVisibility(View.VISIBLE);
		textViewEndOfList.setText(R.string.post_list_error);
	}
}
