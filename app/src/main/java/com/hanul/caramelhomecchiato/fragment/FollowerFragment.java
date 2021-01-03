package com.hanul.caramelhomecchiato.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hanul.caramelhomecchiato.R;
import com.hanul.caramelhomecchiato.adapter.BaseAdapter;
import com.hanul.caramelhomecchiato.data.User;
import com.hanul.caramelhomecchiato.network.UserService;
import com.hanul.caramelhomecchiato.util.Auth;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FollowerFragment extends Fragment{
	private static final String TAG = "FollowerFragment";

	private RecyclerView follower;
	private BaseAdapter<User> adapter;

	@Override
	public View onCreateView(LayoutInflater inflater,
	                         ViewGroup container,
	                         Bundle savedInstanceState){
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_follower, container, false);

		follower = view.findViewById(R.id.recyclerViewFollower);

		// follower.setAdapter(adapter); // TODO setAdapter


		// TODO loginUser?
		UserService.INSTANCE.getFollower(Auth.getInstance().getLoginUser()).enqueue(new Callback<JsonObject>(){
			@Override public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response){
				JsonObject body = response.body();
				if(body.has("error")){
					Log.e(TAG, "getFollower: 예상치 못한 오류: "+body.get("error").getAsString());
					Toast.makeText(getContext(), "예상치 못한 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
					return;
				}
				List<User> usersList = new ArrayList<>();

				JsonArray users = body.get("users").getAsJsonArray();
				for(JsonElement userElement : users){
					JsonObject userObject = userElement.getAsJsonObject();

					int id = userObject.get("id").getAsInt();
					String name = userObject.get("name").getAsString();
					String profileImage = userObject.has("profileImage") ? userObject.get("profileImage").getAsString() : null;

					User u = new User(id, name, profileImage);

					usersList.add(u);
				}
				setUsers(usersList);
			}
			@Override public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t){
				Log.e(TAG, "getFollower: Failure ", t);
				Toast.makeText(getContext(), "예상치 못한 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
			}
		});
		return view;
	}

	private void setUsers(List<User> users){
		adapter.elements().addAll(users);
		adapter.notifyDataSetChanged();
	}
}