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

import org.apache.http.entity.mime.MultipartEntityBuilder;
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
			// TODO 이메일 또는 휴대폰 번호로 로그인
			finish(); // FIXME temp
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
			// TODO ?
		});
	}

	@Override protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode==ACTIVITY_JOIN_SUCCESS){
			if(resultCode==RESULT_OK) setResult(RESULT_OK, data);
			else setResult(RESULT_CANCELED);
			finish();
		}
	}

	private void kakaoLoginCallback(@Nullable OAuthToken oAuthToken, @Nullable Throwable error){
		if(error!=null){
			Log.e(TAG, "카카오톡 로그인 실패", error);
		}else if(oAuthToken!=null){
			//Log.i(TAG, "카카오톡 로그인 성공! "+oAuthToken.getAccessToken());
			new LoginWithKakaoTask(this, oAuthToken).execute();
		}
	}

	private static final class LoginWithKakaoTask extends JsonResponseTask<LoginActivity>{
		private final OAuthToken oAuthToken;

		public LoginWithKakaoTask(LoginActivity context, OAuthToken oAuthToken){
			super(context, "loginWithKakao");
			this.oAuthToken = oAuthToken;
		}

		@Override protected void appendMultipartEntity(MultipartEntityBuilder builder){
			builder.addTextBody("kakaoLoginToken", oAuthToken.getAccessToken());
		}

		@Override protected void onPostExecute(@NotNull LoginActivity context, JsonObject jsonObject){
			context.setResult(RESULT_OK, new Intent().putExtra("userId", jsonObject.get("userId").getAsInt()));
			context.finish();
		}
	}
}