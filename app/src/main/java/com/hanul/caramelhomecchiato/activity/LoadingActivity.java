package com.hanul.caramelhomecchiato.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;
import com.hanul.caramelhomecchiato.CaramelHomecchiatoApp;
import com.hanul.caramelhomecchiato.R;
import com.hanul.caramelhomecchiato.network.LoginService;
import com.hanul.caramelhomecchiato.util.Auth;

import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;

import retrofit2.Call;
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

		CaramelHomecchiatoApp app = (CaramelHomecchiatoApp)getApplication();
		Executor main = ContextCompat.getMainExecutor(this);

		Future<?> clearDiskCacheFuture = app.executorService.submit(() -> Glide.get(app.getApplicationContext()).clearDiskCache());

		String authToken = Auth.getInstance().getAuthToken();

		@Nullable Future<?> tryLoginFuture;
		if(authToken!=null){
			Log.d(TAG, "doInBackground: AuthToken("+authToken+") 발견. 로그인 시도.");

			tryLoginFuture = app.executorService.submit(() -> {
				Call<JsonObject> call = LoginService.INSTANCE.loginWithAuthToken(authToken);
				try{
					Response<JsonObject> response = call.execute();

					if(response.isSuccessful()){
						JsonObject result = Objects.requireNonNull(response.body());
						if(result.has("error")){
							Log.e(TAG, "loginWithAuthToken: AuthToken 로그인 실패: "+result.get("error").getAsString());
						}else{
							Auth.getInstance().setLoginData(result);
						}
					}else{
						Log.e(TAG, "loginWithAuthToken: AuthToken 로그인 실패: "+response.errorBody());
					}
				}catch(Exception ex){
					Log.e(TAG, "loginWithAuthToken: Error ", ex);
				}
			});
		}else{
			Log.d(TAG, "doInBackground: AuthToken 없음.");
			tryLoginFuture = null;
		}

		app.executorService.submit(() -> {
			try{
				Thread.sleep(2000);
				clearDiskCacheFuture.get();
				if(tryLoginFuture!=null) tryLoginFuture.get();

				main.execute(() -> {
					if(Auth.getInstance().getLoginUser()!=null){
						postLoad();
					}else{
						startActivityForResult(new Intent(LoadingActivity.this, LoginActivity.class), LOGIN_REQ);
					}
				});
			}catch(Exception e){
				Log.e(TAG, "onCreate: ", e);
				System.exit(1);
			}
		});
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