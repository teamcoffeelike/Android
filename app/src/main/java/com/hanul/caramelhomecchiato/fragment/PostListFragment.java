package com.hanul.caramelhomecchiato.fragment;

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
import com.hanul.caramelhomecchiato.event.FollowingEvent;
import com.hanul.caramelhomecchiato.network.PostService;
import com.hanul.caramelhomecchiato.util.lifecyclehandler.PostScrollHandler;

import java.util.List;

public class PostListFragment extends Fragment implements PostScrollHandler.Listener<Post>{
	private PostAdapter postAdapter;
	private TextView textViewError;
	private View endOfList;

	private final PostScrollHandler postScrollHandler = new PostScrollHandler(this,
			since -> PostService.INSTANCE.recentPosts(since, 10),
			this);

	@Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.fragment_post_list, container, false);

		textViewError = view.findViewById(R.id.textViewError);
		endOfList = view.findViewById(R.id.endOfList);

		RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
		recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));

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
			FollowingEvent.dispatch(post.getAuthor());
		}
		List<Post> elements = postAdapter.elements();
		if(reset){
			elements.clear();
			elements.addAll(posts);
			postAdapter.notifyDataSetChanged();
		}else{
			int size = elements.size();
			elements.addAll(posts);
			postAdapter.notifyItemRangeInserted(size, posts.size());
		}
		textViewError.setVisibility(View.GONE);
		this.endOfList.setVisibility(endOfList ? View.VISIBLE : View.GONE);
	}

	@Override public void error(){
		textViewError.setVisibility(View.VISIBLE);
		endOfList.setVisibility(View.GONE);
	}
}
