package com.hanul.caramelhomecchiato.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hanul.caramelhomecchiato.R;
import com.hanul.caramelhomecchiato.adapter.SearchFriendAdapter;
import com.hanul.caramelhomecchiato.data.Post;
import com.hanul.caramelhomecchiato.data.User;
import com.hanul.caramelhomecchiato.data.UserProfile;
import com.hanul.caramelhomecchiato.network.NetUtils;
import com.hanul.caramelhomecchiato.network.UserService;
import com.hanul.caramelhomecchiato.util.BaseCallback;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class SearchFriendActivity extends AppCompatActivity{
	private static final String TAG = "SearchFriendActivity";

	private EditText editTextSearchName;
	private SearchFriendAdapter searchFriendAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_friend);

		editTextSearchName = findViewById(R.id.editTextSearchName);
		RecyclerView recyclerView = findViewById(R.id.searchFriendRecyclerView);
		searchFriendAdapter = new SearchFriendAdapter();
		recyclerView.setAdapter(searchFriendAdapter);
		recyclerView.setLayoutManager(new LinearLayoutManager(this));

		editTextSearchName.addTextChangedListener(new TextWatcher(){
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after){}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count){
				Log.d(TAG, "onTextChanged: " + s);
				UserService.INSTANCE.searchUserByName(s.toString()).enqueue(new BaseCallback() {
					@Override public void onSuccessfulResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response, @NonNull JsonObject result) {
						List<User> users = new ArrayList<>();
						for(JsonElement e : result.get("users").getAsJsonArray()){
							users.add(NetUtils.GSON.fromJson(e, User.class));
						}
						setUsers(users);
					}
					@Override public void onErrorResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response, @NonNull String error){
						Log.e(TAG, "profile: 예상치 못한 오류: "+error);
						Toast.makeText(SearchFriendActivity.this, "예상치 못한 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
					}
					@Override public void onFailedResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response){
						Log.e(TAG, "profile: 요청 실패 ");
						Toast.makeText(SearchFriendActivity.this, "예상치 못한 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
					}
					@Override public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t){
						Log.e(TAG, "profile: Failure ", t);
						Toast.makeText(SearchFriendActivity.this, "예상치 못한 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
					}
				});
			}

			@Override
			public void afterTextChanged(Editable s){}
		});

	}

	private void setUsers(List<User> users) {
		List<User> elements = searchFriendAdapter.elements();
		elements.clear();
		elements.addAll(users);
		searchFriendAdapter.notifyDataSetChanged();
	}
}