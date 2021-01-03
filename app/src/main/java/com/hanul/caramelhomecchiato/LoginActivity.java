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
import com.hanul.caramelhomecchiato.network.KakaoIntegrationService;
import com.hanul.caramelhomecchiato.network.LoginService;
import com.hanul.caramelhomecchiato.util.Auth;
import com.hanul.caramelhomecchiato.util.KakaoApiUtils;
import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.user.model.Account;
import com.kakao.sdk.user.model.Profile;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity{
	private static final String TAG = "LoginActivity";

	private static final int LOGIN_RESULT = 1;

	private ProgressDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		dialog = new ProgressDialog(this);
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);

		EditText editTextId = findViewById(R.id.editTextId);
		EditText editTextPassword = findViewById(R.id.editTextPassword);

		findViewById(R.id.buttonLogin).setOnClickListener(v -> {
			Log.d(TAG, "onCreate: buttonLogin click");
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

			dialog.show();

			if(isIdEmail){
				LoginService.INSTANCE.loginWithEmail(id, pw).enqueue(new Callback<JsonObject>(){
					@Override public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response){
						loginCallback(response.body());
					}
					@Override public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t){
						dialog.dismiss();
					}
				});
			}else{
				LoginService.INSTANCE.loginWithPhoneNumber(id, pw).enqueue(new Callback<JsonObject>(){
					@Override public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response){
						loginCallback(response.body());
					}
					@Override public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t){
						dialog.dismiss();
					}
				});
			}
		});
		findViewById(R.id.buttonJoin).setOnClickListener(v -> {
			startActivityForResult(new Intent(this, JoinActivity.class), LOGIN_RESULT);
		});

		findViewById(R.id.textViewFindPassword).setOnClickListener(v -> {
			startActivity(new Intent(this, FindPasswordActivity.class));
		});

		findViewById(R.id.buttonLoginWithKakao).setOnClickListener(v -> {
			KakaoApiUtils.loginWithKakao(this, (oAuthToken, error) -> {
				if(error!=null){
					Log.e(TAG, "카카오톡 로그인 실패", error);
				}else if(oAuthToken!=null){
					dialog.show();
					KakaoIntegrationService.INSTANCE.loginWithKakao(oAuthToken.getAccessToken()).enqueue(new Callback<JsonObject>(){
						@Override public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response){
							kakaoLoginCallback(oAuthToken, response.body());
						}
						@Override public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t){
							Log.e(TAG, "loginWithKakao: Failure", t);
							dialog.dismiss();
						}
					});
				}
			});
		});
	}

	@Override
	protected void onDestroy(){
		super.onDestroy();
		if(dialog!=null){
			dialog.dismiss();
			dialog = null;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode==LOGIN_RESULT){
			if(resultCode==RESULT_OK) finish();
		}
	}

	private void loginCallback(JsonObject jsonObject){
		if(jsonObject.has("error")){
			String error = jsonObject.get("error").getAsString();
			if("login_failed".equals(error)){
				Toast.makeText(this, "이메일, 휴대전화 번호 또는 비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
			}else{
				Toast.makeText(this, "예상치 못한 오류가 발생했습니다: "+error, Toast.LENGTH_SHORT).show();
			}
			dialog.dismiss();
			return;
		}

		Auth.getInstance().setLoginData(jsonObject);
		setResult(RESULT_OK);
		finish();
	}

	private void kakaoLoginCallback(OAuthToken oAuthToken, JsonObject jsonObject){
		if(jsonObject.has("error")){
			String loginError = jsonObject.get("error").getAsString();
			switch(loginError){
				case "needs_agreement": // 프로필 이용을 위한 동의가 필요
					fetchProfileAndJoin(oAuthToken, true);
					break;
				case "bad_kakao_login_token":
					Toast.makeText(this, "카카오 로그인이 해제되었습니다. 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
					dialog.dismiss();
					break;
				case "kakao_service_unavailable":
					Toast.makeText(this, "카카오 연동 서비스를 제공할 수 없습니다.", Toast.LENGTH_SHORT).show();
					dialog.dismiss();
					break;
				default:
					Log.e(TAG, "kakaoLoginCallback: "+loginError);
					Toast.makeText(this, "예상치 못한 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
					dialog.dismiss();
					break;
			}
			return;
		}

		Auth.getInstance().setLoginData(jsonObject);
		setResult(RESULT_OK);
		finish();
	}

	private void fetchProfileAndJoin(OAuthToken oAuthToken, boolean retry){
		KakaoApiUtils.getUser((user, error) -> {
			if(error!=null){
				Log.e(TAG, "fetchProfileAndJoin: 사용자 정보 요청 실패", error);
				dialog.dismiss();
				return;
			}

			Account acc = user.getKakaoAccount();
			if(acc!=null){
				Profile profile = acc.getProfile();
				if(profile!=null){
					// 닉네임 확인, 이름 명시하여 재시도
					KakaoIntegrationService.INSTANCE.joinWithKakao(oAuthToken.getAccessToken(), profile.getNickname()).enqueue(new Callback<JsonObject>(){
						@Override public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response){
							loginCallback(response.body());
						}
						@Override public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t){
							dialog.dismiss();
						}
					});
					return;
				}else if(acc.getProfileNeedsAgreement()){ // 동의 필요
					if(retry){
						// 동의 후 재시도
						KakaoApiUtils.loginWithNewScopes(this,
								(token, error2) -> fetchProfileAndJoin(token, false),
								"profile");
						return;
					}
				}
			}

			// 거부당함
			startActivityForResult(new Intent(this, JoinKakaoWithNameActivity.class)
							.putExtra(JoinKakaoWithNameActivity.EXTRA_KAKAO_AUTO_TOKEN, oAuthToken),
					LOGIN_RESULT);
		});
	}
}