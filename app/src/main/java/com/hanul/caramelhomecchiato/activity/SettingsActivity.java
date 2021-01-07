package com.hanul.caramelhomecchiato.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;
import com.hanul.caramelhomecchiato.R;
import com.hanul.caramelhomecchiato.data.UserProfile;
import com.hanul.caramelhomecchiato.network.LoginService;
import com.hanul.caramelhomecchiato.network.NetUtils;
import com.hanul.caramelhomecchiato.network.UserService;
import com.hanul.caramelhomecchiato.util.Auth;
import com.hanul.caramelhomecchiato.util.BaseCallback;
import com.hanul.caramelhomecchiato.util.SpinnerHandler;

import retrofit2.Call;
import retrofit2.Response;

public class SettingsActivity extends AppCompatActivity{
	private static final String TAG = "SettingsActivity";

	private final SpinnerHandler spinnerHandler = new SpinnerHandler(this);

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

		findViewById(R.id.buttonEditProfileImage).setOnClickListener(v -> {
			spinnerHandler.show();
			UserService.INSTANCE.profile(Auth.getInstance().expectLoginUser()).enqueue(new BaseCallback(){
				@Override public void onSuccessfulResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response, @NonNull JsonObject result){
					spinnerHandler.dismiss();
					startActivity(new Intent(SettingsActivity.this, EditProfileActivity.class)
							.putExtra(EditProfileActivity.EXTRA_PROFILE, NetUtils.GSON.fromJson(result, UserProfile.class)));
				}
				@Override public void onErrorResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response, @NonNull String error){
					Log.e(TAG, "profile: 에러 발생 "+error);
					Toast.makeText(SettingsActivity.this, "예상치 못한 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
					spinnerHandler.dismiss();
				}
				@Override public void onFailedResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response){
					Log.e(TAG, "profile: Failure: "+response.errorBody());
					Toast.makeText(SettingsActivity.this, "예상치 못한 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
					spinnerHandler.dismiss();
				}
				@Override public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t){
					Log.e(TAG, "profile: Failure", t);
					Toast.makeText(SettingsActivity.this, "예상치 못한 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
					spinnerHandler.dismiss();
				}
			});
		});

		/* 비밀번호 변경 버튼 -> 비밀번호 변경 화면으로 이동 */
		findViewById(R.id.buttonChangePassword).setOnClickListener(v -> {
			startActivity(new Intent(this, ChangePasswordActivity.class));
		});

		findViewById(R.id.buttonLogout).setOnClickListener(v -> {
			new AlertDialog.Builder(this)
					.setMessage("정말 로그아웃하시겠습니까?")
					.setPositiveButton("예", (dialog, which) -> {
						spinnerHandler.show();
						LoginService.INSTANCE.logout().enqueue(new BaseCallback(){
							@Override public void onSuccessfulResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response, @NonNull JsonObject result){
								displayLogoutMessage();
							}
							@Override public void onErrorResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response, @NonNull String error){
								Log.e(TAG, "logout: 에러 발생 "+error);
								displayLogoutMessage();
							}
							@Override public void onFailedResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response){
								Log.e(TAG, "logout: Failure: "+response.errorBody());
								Toast.makeText(SettingsActivity.this, "예상치 못한 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
								spinnerHandler.dismiss();
							}
							@Override public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t){
								Log.e(TAG, "logout: Failure", t);
								Toast.makeText(SettingsActivity.this, "예상치 못한 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
								spinnerHandler.dismiss();
							}
						});
					})
					.setNegativeButton("아니오", (dialog, which) -> {})
					.show();
		});
	}

	private void displayLogoutMessage(){
		spinnerHandler.dismiss();
		Auth.getInstance().removeLoginData();
		new AlertDialog.Builder(SettingsActivity.this)
				.setMessage("로그아웃되었습니다.")
				.setNeutralButton("알겠어요", (dialog1, which1) -> {})
				.setOnDismissListener(dialog1 -> {
					startActivity(new Intent(getApplicationContext(), LoadingActivity.class)
							.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK));
				})
				.show();
	}
}