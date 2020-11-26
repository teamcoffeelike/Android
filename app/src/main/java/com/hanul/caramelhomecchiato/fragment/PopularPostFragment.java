package com.hanul.caramelhomecchiato.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hanul.caramelhomecchiato.R;
import com.hanul.caramelhomecchiato.adapter.PostAdapter;
import com.hanul.caramelhomecchiato.data.Post;
import com.hanul.caramelhomecchiato.data.Reaction;
import com.hanul.caramelhomecchiato.data.User;

import java.util.Collections;
import java.util.List;

public class PopularPostFragment extends Fragment{
	@Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.fragment_popular_post, container, false);

		Context context = getContext();
		if(context!=null){
			RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
			recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));

			PostAdapter adapter = new PostAdapter();

			List<Post> posts = adapter.elements();
			posts.add(new Post(1, new User(1, "A", null), Collections.emptyList(), "가나다", 3));
			posts.add(new Post(2, new User(2, "B", null), Collections.emptyList(), "가나다", 3));
			posts.add(new Post(3, new User(3, "C", null), Collections.emptyList(), "가나다", 3));
			posts.add(new Post(4, new User(4, "D", null), Collections.emptyList(), "가나다", 3));
			posts.add(new Post(5, new User(5, "E", null), Collections.emptyList(), "가나다", 3));

			recyclerView.setAdapter(adapter);
		}
		return view;
	}
}
