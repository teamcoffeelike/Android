package com.hanul.caramelhomecchiato;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.hanul.caramelhomecchiato.task.LoginWithAuthTokenTask;
import com.hanul.caramelhomecchiato.util.Auth;

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
			new LoginWithAuthTokenTask<>(this, authToken)
					.onSucceed((activity, o) -> {
						if(o.has("error")){
							Log.d(TAG, "doInBackground: AuthToken 로그인 실패: "+o.get("error").getAsString());
							activity.startActivityForResult(new Intent(activity, LoginActivity.class), LOGIN_REQ);
						}else{
							Auth.getInstance().setLoginData(o);
							activity.postLoad();
						}
					})
					.onCancelled((activity, jsonObject1) -> {
						activity.startActivityForResult(new Intent(activity, LoginActivity.class), LOGIN_REQ);
					}).execute();
		}else startActivityForResult(new Intent(this, LoginActivity.class), LOGIN_REQ);
	}

	@Override protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode==LOGIN_REQ){
			if(resultCode==RESULT_OK){
				if(Auth.getInstance().getAuthToken()==null){
					throw new IllegalStateException("로그인 Activity가 추가적인 데이터 처리 없이 OK를 반환했습니다.");
				}
				postLoad();
			}else{ // 로그인 캔슬, 프로그램 종료
				finish();
			}
		}
	}

	private void postLoad(){
		startActivity(new Intent(this, MainActivity.class));
		finish();
	}
}