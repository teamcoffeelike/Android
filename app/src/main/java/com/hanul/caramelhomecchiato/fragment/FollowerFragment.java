package com.hanul.caramelhomecchiato.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hanul.caramelhomecchiato.R;
import com.hanul.caramelhomecchiato.adapter.BaseAdapter;
import com.hanul.caramelhomecchiato.data.User;
import com.hanul.caramelhomecchiato.task.BaseTask;
import com.hanul.caramelhomecchiato.task.GetFollowerTask;

import java.util.ArrayList;
import java.util.List;

public class FollowerFragment extends Fragment {
	private RecyclerView follower;
	private BaseAdapter<User> adapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_follower, container, false);

		follower = view.findViewById(R.id.recyclerViewFollower);

		follower.setAdapter();

		new GetFollowerTask<>(this)
			.onSucceed(new BaseTask.BaseTaskCallback<FollowerFragment, JsonObject>(){
				@Override public void onFinish(@NonNull FollowerFragment followerFragment, JsonObject jsonObject){
					List<User> usersList = new ArrayList<>();

					JsonArray users = jsonObject.get("users").getAsJsonArray();
					for(JsonElement userElement : users){
						JsonObject userObject = userElement.getAsJsonObject();

						int id = userObject.get("id").getAsInt();
						String name = userObject.get("name").getAsString();
						String profileImage = userObject.has("profileImage") ? userObject.get("profileImage").getAsString() : null;

						User u = new User(id, name, profileImage);

						usersList.add(u);
					}
					followerFragment.setUsers(usersList);
				}
			})
			.execute();

		return view;
	}

	private void setUsers(List<User> users){
		adapter.elements().addAll(users);
		adapter.notifyDataSetChanged();
	}
}