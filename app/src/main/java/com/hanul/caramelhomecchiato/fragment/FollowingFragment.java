package com.hanul.caramelhomecchiato.fragment;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hanul.caramelhomecchiato.data.User;
import com.hanul.caramelhomecchiato.network.NetUtils;
import com.hanul.caramelhomecchiato.network.UserService;
import com.hanul.caramelhomecchiato.util.BaseCallback;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class FollowingFragment extends FollowerFragment{
	private static final String TAG = "FollowingFragment";

	public static FollowingFragment newInstance(int userId){
		FollowingFragment f = new FollowingFragment();
		Bundle args = new Bundle();
		args.putInt(EXTRA_USER_ID, userId);
		f.setArguments(args);
		return f;
	}

	@Override protected void enqueueTransaction(int userId){
		UserService.INSTANCE.getFollowing(userId).enqueue(new BaseCallback(){
			@Override public void onSuccessfulResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response, @NonNull JsonObject result){
				List<User> usersList = new ArrayList<>();

				JsonArray users = result.get("users").getAsJsonArray();
				for(JsonElement userElement : users){
					usersList.add(NetUtils.GSON.fromJson(userElement, User.class));
				}
				setUsers(usersList);
			}
			@Override public void onErrorResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response, @NonNull String error){
				Log.e(TAG, "getFollowing: Error: "+error);
				Toast.makeText(getContext(), "예상치 못한 오류가 발생하여 팔로잉 유저를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show();
			}
			@Override public void onFailedResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response){
				Log.e(TAG, "getFollowing: Failure: "+response.errorBody());
				Toast.makeText(getContext(), "예상치 못한 오류가 발생하여 팔로잉 유저를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show();
			}
			@Override public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t){
				Log.e(TAG, "getFollowing: Unexpected", t);
				Toast.makeText(getContext(), "예상치 못한 오류가 발생하여 팔로잉 유저를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show();
			}
		});
	}
}