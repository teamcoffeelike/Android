package com.hanul.caramelhomecchiato;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;
import com.hanul.caramelhomecchiato.task.JsonResponseTask;
import com.kakao.sdk.auth.LoginClient;
import com.kakao.sdk.auth.model.OAuthToken;

import org.jetbrains.annotations.NotNull;

import kotlin.Unit;

public class LoginActivity extends AppCompatActivity{
	private static final String TAG = "LoginActivity";

	private static final int ACTIVITY_JOIN_SUCCESS = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		findViewById(R.id.buttonLogin).setOnClickListener(v -> {

			finish();
		});
		findViewById(R.id.buttonJoin).setOnClickListener(v -> {
			startActivityForResult(new Intent(this, JoinActivity.class), ACTIVITY_JOIN_SUCCESS);
		});

		findViewById(R.id.textViewFindLoginIdPw).setOnClickListener(v -> startActivity(new Intent(this, FindLoginIdPwActivity.class)));

		findViewById(R.id.buttonLoginWithKakao).setOnClickListener(v -> {
			if(LoginClient.getInstance().isKakaoTalkLoginAvailable(this)){
				LoginClient.getInstance().loginWithKakaoTalk(this, (oAuthToken, error) -> {
					kakaoLoginCallback(oAuthToken, error);
					return Unit.INSTANCE; // ㅋㅋ
				});
			}else{
				LoginClient.getInstance().loginWithKakaoAccount(this, (oAuthToken, error) -> {
					kakaoLoginCallback(oAuthToken, error);
					return Unit.INSTANCE; // ㅋㅋ
				});
			}
		});
		findViewById(R.id.buttonLoginWithNaver).setOnClickListener(v -> {

		});
	}

	@Override protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode==ACTIVITY_JOIN_SUCCESS){
			if(resultCode==RESULT_OK) setResult(RESULT_OK, data);
			else setResult(RESULT_CANCELED);
		}
	}

	private void kakaoLoginCallback(@Nullable OAuthToken oAuthToken, @Nullable Throwable error){
		if(error!=null){
			Log.e(TAG, "카카오톡 로그인 실패", error);
		}else if(oAuthToken!=null){
			//Log.i(TAG, "카카오톡 로그인 성공! "+oAuthToken.getAccessToken());
			new JoinWithKakaoTask(this).execute();
		}
	}

	private static final class JoinWithKakaoTask extends JsonResponseTask<LoginActivity>{
		public JoinWithKakaoTask(LoginActivity context){
			super(context, "JoinWithKakao");
		}

		@Override protected void onPostExecute(@NotNull LoginActivity context, JsonObject jsonObject){
			context.setResult(RESULT_OK, new Intent().putExtra("userId", jsonObject.get("userId").getAsInt()));
		}
	}
}