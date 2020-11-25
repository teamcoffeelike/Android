package com.hanul.caramelhomecchiato.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.hanul.caramelhomecchiato.R;
import com.hanul.caramelhomecchiato.data.Post;

public class PostFragment extends Fragment{
	public static PostFragment newInstance(Post post){
		PostFragment f = new PostFragment();
		Bundle args = new Bundle();
		args.putParcelable("post", post);
		f.setArguments(args);
		return f;
	}

	@Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.view_post, container);

		return view;
	}
}
