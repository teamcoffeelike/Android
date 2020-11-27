package com.hanul.caramelhomecchiato.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hanul.caramelhomecchiato.EditProfileActivity;
import com.hanul.caramelhomecchiato.R;
import com.hanul.caramelhomecchiato.WritePostActivity;
import com.hanul.caramelhomecchiato.adapter.ProfilePostAdapter;
import com.hanul.caramelhomecchiato.data.Post;
import com.hanul.caramelhomecchiato.data.User;

import java.util.Collections;
import java.util.List;

public class ProfileFragment extends Fragment{
	@Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.fragment_profile, container, false);

		/* 프로필 편집 버튼 */
		Button btnEditProfile = view.findViewById(R.id.buttonEditProfile);
		btnEditProfile.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v){
				Intent intent = new Intent(getContext(), EditProfileActivity.class);
				startActivity(intent);
			}
		});


		RecyclerView recyclerView = view.findViewById(R.id.recyclerView);

		Context ctx = getContext();
		if(ctx!=null){
			view.findViewById(R.id.buttonNewPost).setOnClickListener(v -> startActivity(new Intent(ctx, WritePostActivity.class)));

			recyclerView.setLayoutManager(new GridLayoutManager(ctx, 3, RecyclerView.VERTICAL, false));

			ProfilePostAdapter adapter = new ProfilePostAdapter();
			List<Post> elements = adapter.elements();
			for(int i = 0; i<45; i++) elements.add(new Post(i, new User(1, "", null), Collections.emptyList(), "", 0));
			recyclerView.setAdapter(adapter);
		}


		return view;
	}
}
