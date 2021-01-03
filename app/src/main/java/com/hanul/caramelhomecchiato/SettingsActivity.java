package com.hanul.caramelhomecchiato;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;
import com.hanul.caramelhomecchiato.network.LoginService;
import com.hanul.caramelhomecchiato.util.Auth;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsActivity extends AppCompatActivity{
	private static final String TAG = "SettingsActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

		findViewById(R.id.buttonEditProfile).setOnClickListener(v -> {
			startActivity(new Intent(this, EditProfileActivity.class));
		});

		/* 비밀번호 변경 버튼 -> 비밀번호 변경 화면으로 이동 */
		findViewById(R.id.buttonChangePassword).setOnClickListener(v -> {
			startActivity(new Intent(this, ChangePasswordActivity.class));
		});

		findViewById(R.id.buttonLogout).setOnClickListener(v -> {
			new AlertDialog.Builder(this)
					.setMessage("정말 로그아웃하시겠습니까?")
					.setPositiveButton("예", (dialog, which) -> {
						LoginService.INSTANCE.logout().enqueue(new Callback<JsonObject>(){
							@Override public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response){
								JsonObject body = response.body();
								if(body.has("error")){
									Log.e(TAG, "logout: 에러 발생 "+body.get("error").getAsString());
								}
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
							@Override public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t){
								// TODO
								Log.e(TAG, "logout: Failure", t);
								Toast.makeText(SettingsActivity.this, "예상치 못한 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
							}
						});
					})
					.setNegativeButton("아니오", (dialog, which) -> {})
					.show();
		});
	}
}