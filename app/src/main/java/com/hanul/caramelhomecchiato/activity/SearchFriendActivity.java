package com.hanul.caramelhomecchiato.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hanul.caramelhomecchiato.R;
import com.hanul.caramelhomecchiato.adapter.UserViewAdapter;
import com.hanul.caramelhomecchiato.data.User;
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
	private UserViewAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_friend);

		editTextSearchName = findViewById(R.id.editTextSearchName);
		RecyclerView recyclerView = findViewById(R.id.searchFriendRecyclerView);
		adapter = new UserViewAdapter();
		recyclerView.setAdapter(adapter);
		recyclerView.setLayoutManager(new LinearLayoutManager(this));

		editTextSearchName.addTextChangedListener(new TextWatcher(){
			@Override public void beforeTextChanged(CharSequence s, int start, int count, int after){}

			@Override public void onTextChanged(CharSequence s, int start, int before, int count){
				if(s.length()==0){
					clear();
				}else UserService.INSTANCE.searchUserByName(s.toString()).enqueue(new BaseCallback(){
					@Override public void onSuccessfulResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response, @NonNull JsonObject result){
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

			@Override public void afterTextChanged(Editable s){}
		});
	}

	private void setUsers(List<User> users){
		List<User> elements = adapter.elements();
		elements.clear();
		elements.addAll(users);
		adapter.notifyDataSetChanged();
	}

	private void clear(){
		List<User> elements = adapter.elements();
		elements.clear();
		adapter.notifyDataSetChanged();
	}
}