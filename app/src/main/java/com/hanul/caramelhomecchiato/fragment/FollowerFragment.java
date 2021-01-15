package com.hanul.caramelhomecchiato.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hanul.caramelhomecchiato.R;
import com.hanul.caramelhomecchiato.adapter.BaseAdapter;
import com.hanul.caramelhomecchiato.adapter.UserViewAdapter;
import com.hanul.caramelhomecchiato.data.User;
import com.hanul.caramelhomecchiato.network.NetUtils;
import com.hanul.caramelhomecchiato.network.UserService;
import com.hanul.caramelhomecchiato.util.BaseCallback;
import com.hanul.caramelhomecchiato.event.FollowingEvent;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class FollowerFragment extends Fragment{
	private static final String TAG = "FollowerFragment";

	public static final String EXTRA_USER_ID = "userId";

	public static FollowerFragment newInstance(int userId){
		FollowerFragment f = new FollowerFragment();
		Bundle args = new Bundle();
		args.putInt(EXTRA_USER_ID, userId);
		f.setArguments(args);
		return f;
	}

	private int userId;
	private BaseAdapter<User> adapter;

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater,
	                         @Nullable ViewGroup container,
	                         @Nullable Bundle savedInstanceState){
		Bundle arguments = getArguments();
		userId = arguments==null ? 0 : arguments.getInt(EXTRA_USER_ID);
		if(userId==0) throw new IllegalStateException(getClass().getSimpleName()+"에 유저 ID 제공되지 않음");

		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_follower, container, false);

		RecyclerView recyclerViewFollower = view.findViewById(R.id.recyclerViewFollower);
		adapter = new UserViewAdapter();

		recyclerViewFollower.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
		recyclerViewFollower.setAdapter(adapter);

		return view;
	}

	@Override public void onResume(){
		super.onResume();
		setUsers(null);
		enqueueTransaction(userId);
	}

	protected void enqueueTransaction(int userId){
		UserService.INSTANCE.getFollower(userId).enqueue(new BaseCallback(){
			@Override public void onSuccessfulResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response, @NonNull JsonObject result){
				List<User> usersList = new ArrayList<>();

				JsonArray users = result.get("users").getAsJsonArray();
				for(JsonElement userElement : users){
					User user = NetUtils.GSON.fromJson(userElement, User.class);
					usersList.add(user);
					FollowingEvent.dispatch(user);
				}
				setUsers(usersList);
			}
			@Override public void onErrorResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response, @NonNull String error){
				Log.e(TAG, "getFollower: Error: "+error);
				Toast.makeText(getContext(), "예상치 못한 오류가 발생하여 팔로워를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show();
			}
			@Override public void onFailedResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response){
				Log.e(TAG, "getFollower: Failure: "+response.errorBody());
				Toast.makeText(getContext(), "예상치 못한 오류가 발생하여 팔로워를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show();
			}
			@Override public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t){
				Log.e(TAG, "getFollower: Unexpected", t);
				Toast.makeText(getContext(), "예상치 못한 오류가 발생하여 팔로워를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show();
			}
		});
	}

	protected void setUsers(@Nullable List<User> users){
		List<User> elements = adapter.elements();
		elements.clear();
		if(users!=null) elements.addAll(users);
		adapter.notifyDataSetChanged();
	}
}