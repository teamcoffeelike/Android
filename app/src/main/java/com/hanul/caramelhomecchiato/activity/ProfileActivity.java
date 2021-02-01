package com.hanul.caramelhomecchiato.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;
import com.hanul.caramelhomecchiato.R;
import com.hanul.caramelhomecchiato.data.UserProfile;
import com.hanul.caramelhomecchiato.event.FollowingEvent;
import com.hanul.caramelhomecchiato.fragment.ProfileFragment;
import com.hanul.caramelhomecchiato.network.NetUtils;
import com.hanul.caramelhomecchiato.network.UserService;
import com.hanul.caramelhomecchiato.util.BaseCallback;

import retrofit2.Call;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity{
	private static final String TAG = "ProfileActivity";

	public static final int WRITE_POST_ACTIVITY = 1;

	public static final String EXTRA_USER_ID = "userId";

	private int userId;
	private ProfileFragment fragment;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);

		userId = getIntent().getIntExtra(EXTRA_USER_ID, -1);
		if(userId==-1){
			throw new IllegalStateException("ProfileActivity에 유저 ID 제공되지 않음");
		}

		fragment = new ProfileFragment();
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.root, fragment)
				.commit();
	}

	@Override protected void onResume(){
		super.onResume();
		UserService.INSTANCE.profile(userId).enqueue(new BaseCallback(){
			@Override public void onSuccessfulResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response, @NonNull JsonObject result){
				UserProfile profile = NetUtils.GSON.fromJson(result, UserProfile.class);
				fragment.setProfile(profile);
				FollowingEvent.dispatch(profile);
			}
			@Override public void onErrorResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response, @NonNull String error){
				Log.e(TAG, "profile: Error: "+error);
				Toast.makeText(ProfileActivity.this, "예상치 못한 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
			}
			@Override public void onFailedResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response){
				Log.e(TAG, "profile: 요청 실패: "+response.errorBody());
				Toast.makeText(ProfileActivity.this, "예상치 못한 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
			}
			@Override public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t){
				Log.e(TAG, "profile: Unexpected", t);
				Toast.makeText(ProfileActivity.this, "예상치 못한 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
			}
		});
	}
}
