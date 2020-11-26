package com.hanul.caramelhomecchiato.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hanul.caramelhomecchiato.R;
import com.hanul.caramelhomecchiato.adapter.ProfilePostAdapter;
import com.hanul.caramelhomecchiato.data.Post;
import com.hanul.caramelhomecchiato.data.User;

import java.util.Collections;
import java.util.List;

public class ProfileFragment extends Fragment{
	@Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.fragment_profile, container, false);

		RecyclerView recyclerView = view.findViewById(R.id.recyclerView);

		Context ctx = getContext();
		if(ctx!=null){
			recyclerView.setLayoutManager(new GridLayoutManager(ctx, 3, RecyclerView.VERTICAL, false));


			ProfilePostAdapter adapter = new ProfilePostAdapter();
			List<Post> elements = adapter.elements();
			for(int i=0; i<45; i++) elements.add(new Post(i, new User(1, "", null), Collections.emptyList(), "", 0));
			recyclerView.setAdapter(adapter);
		}


		return view;
	}
}
