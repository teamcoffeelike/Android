package com.hanul.caramelhomecchiato;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;
import com.hanul.caramelhomecchiato.network.LoginService;
import com.hanul.caramelhomecchiato.util.Auth;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 앱의 시작 Activity.
 */
public class LoadingActivity extends AppCompatActivity{
	private static final String TAG = "LoadingActivity";

	private static final int LOGIN_REQ = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_loading);

		String authToken = Auth.getInstance().getAuthToken();
		if(authToken!=null){
			Log.d(TAG, "doInBackground: AuthToken("+authToken+") 발견. 로그인 시도.");

			Call<JsonObject> call = LoginService.INSTANCE.loginWithAuthToken(authToken);
			call.enqueue(new Callback<JsonObject>(){
				@Override public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response){
					JsonObject body = response.body();
					if(body.has("error")){
						Log.e(TAG, "loginWithAuthToken: AuthToken 로그인 실패: "+body.get("error").getAsString());
						startActivityForResult(new Intent(LoadingActivity.this, LoginActivity.class), LOGIN_REQ);
						return;
					}
					Auth.getInstance().setLoginData(body);
					postLoad();
				}
				@Override public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t){
					Log.e(TAG, "loginWithAuthToken: Error ", t);
					startActivityForResult(new Intent(LoadingActivity.this, LoginActivity.class), LOGIN_REQ);
				}
			});
		}else{
			Log.d(TAG, "doInBackground: AuthToken 없음.");
			startActivityForResult(new Intent(this, LoginActivity.class), LOGIN_REQ);
		}
	}

	@Override protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode==LOGIN_REQ){
			if(resultCode==RESULT_OK){
				if(Auth.getInstance().getAuthToken()==null){
					throw new IllegalStateException("로그인 Activity가 추가적인 데이터 처리 없이 OK를 반환했습니다.");
				}
				postLoad();
			}else{
				Log.i(TAG, "onActivityResult: 로그인 캔슬");
				finish();
			}
		}
	}

	private void postLoad(){
		startActivity(new Intent(this, MainActivity.class));
		finish();
	}
}