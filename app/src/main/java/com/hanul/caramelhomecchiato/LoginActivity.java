package com.hanul.caramelhomecchiato;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;
import com.hanul.caramelhomecchiato.task.JsonResponseTask;
import com.hanul.caramelhomecchiato.util.NetUtils;
import com.kakao.sdk.auth.model.OAuthToken;

import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.jetbrains.annotations.NotNull;

public class LoginActivity extends AppCompatActivity{
	private static final String TAG = "LoginActivity";

	private static final int JOIN_REQ = 1;

	private ProgressDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		dialog = new ProgressDialog(this);

		EditText editTextId = findViewById(R.id.editTextId);
		EditText editTextPassword = findViewById(R.id.editTextPassword);

		findViewById(R.id.buttonLogin).setOnClickListener(v -> {
			String id = editTextId.getText().toString().trim();
			String pw = editTextPassword.getText().toString().trim();
			if(id.isEmpty()){
				Toast.makeText(this, "이메일 또는 휴대전화 번호를 입력하세요.", Toast.LENGTH_SHORT).show();
				return;
			}
			if(pw.isEmpty()){
				Toast.makeText(this, "비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show();
				return;
			}

			boolean isIdEmail = id.indexOf('@') >= 0;

			if(isIdEmail){
				new LoginWithEmailTask(this, id, pw).execute();
			}else{
				new LoginWithPhoneNumberTask(this, id, pw).execute();
			}
			dialog.show();
		});
		findViewById(R.id.buttonJoin).setOnClickListener(v -> {
			startActivityForResult(new Intent(this, JoinActivity.class), JOIN_REQ);
		});

		findViewById(R.id.textViewFindLoginIdPw).setOnClickListener(v -> {
			startActivity(new Intent(this, FindLoginIdPwActivity.class));
		});

		findViewById(R.id.buttonLoginWithKakao).setOnClickListener(v -> {
			NetUtils.loginWithKakao(this, (oAuthToken, error) -> {
				if(error!=null){
					Log.e(TAG, "카카오톡 로그인 실패", error);
				}else if(oAuthToken!=null){
					//Log.i(TAG, "카카오톡 로그인 성공! "+oAuthToken.getAccessToken());
					new LoginWithKakaoTask(this, oAuthToken).execute();
					dialog.show();
				}
			});
		});
		findViewById(R.id.buttonLoginWithNaver).setOnClickListener(v -> {
			// TODO ?
		});
	}

	@Override protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode==JOIN_REQ){
			if(resultCode==RESULT_OK){
				int userId = data==null ? 0 : data.getIntExtra("userId", 0);
				if(userId==0){
					Log.e(TAG, "onActivityResult: 회원가입 Activity가 OK를 반환하였으나 추가적인 데이터를 반환하지 않았습니다.");
					setResult(RESULT_CANCELED);
					finish();
				}
				setResult(RESULT_OK, new Intent().putExtra("userId", userId));
				finish();
			}
		}
	}

	private void loginCallback(JsonObject jsonObject){
		if(jsonObject.get(NetUtils.SUCCESS).getAsBoolean()){
			setResult(RESULT_OK, new Intent().putExtra("userId", jsonObject.get("userId").getAsInt()));
			finish();
		}else{
			String error = jsonObject.get("error").getAsString();
			if("login_failed".equals(error)){
				Toast.makeText(this, "이메일, 휴대전화 번호 또는 비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
			}else{
				Toast.makeText(this, "예상치 못한 오류가 발생했습니다: "+error, Toast.LENGTH_SHORT).show();
			}
		}
	}


	private static final class LoginWithEmailTask extends JsonResponseTask<LoginActivity>{
		private final String email;
		private final String password;

		public LoginWithEmailTask(LoginActivity activity, String email, String password){
			super(activity, "loginWithEmail");
			this.email = email;
			this.password = password;
		}

		@Override protected void appendMultipartEntity(MultipartEntityBuilder builder){
			builder.addTextBody("email", email).addTextBody("password", password);
		}
		@Override protected void onPostExecute(@NonNull LoginActivity activity, JsonObject jsonObject){
			activity.dialog.dismiss();
			activity.loginCallback(jsonObject);
		}

		@Override protected void onCancelled(@NonNull LoginActivity activity, JsonObject jsonObject){
			activity.dialog.dismiss();
		}
	}

	private static final class LoginWithPhoneNumberTask extends JsonResponseTask<LoginActivity>{
		private final String phoneNumber;
		private final String password;

		public LoginWithPhoneNumberTask(LoginActivity activity, String phoneNumber, String password){
			super(activity, "loginWithPhoneNumber");
			this.phoneNumber = phoneNumber;
			this.password = password;
		}

		@Override protected void appendMultipartEntity(MultipartEntityBuilder builder){
			builder.addTextBody("phoneNumber", phoneNumber).addTextBody("password", password);
		}
		@Override protected void onPostExecute(@NonNull LoginActivity activity, JsonObject jsonObject){
			activity.dialog.dismiss();
			activity.loginCallback(jsonObject);
		}

		@Override protected void onCancelled(@NonNull LoginActivity activity, JsonObject jsonObject){
			activity.dialog.dismiss();
		}
	}

	private static final class LoginWithKakaoTask extends JsonResponseTask<LoginActivity>{
		private final OAuthToken oAuthToken;

		public LoginWithKakaoTask(LoginActivity activity, OAuthToken oAuthToken){
			super(activity, "loginWithKakao");
			this.oAuthToken = oAuthToken;
		}

		@Override protected void appendMultipartEntity(MultipartEntityBuilder builder){
			builder.addTextBody("kakaoLoginToken", oAuthToken.getAccessToken());
		}

		@Override protected void onPostExecute(@NotNull LoginActivity activity, JsonObject jsonObject){
			activity.dialog.dismiss();
			activity.loginCallback(jsonObject);
		}

		@Override protected void onCancelled(@NonNull LoginActivity activity, JsonObject jsonObject){
			activity.dialog.dismiss();
		}
	}
}