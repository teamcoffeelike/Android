package com.hanul.caramelhomecchiato;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;
import com.hanul.caramelhomecchiato.network.KakaoIntegrationService;
import com.hanul.caramelhomecchiato.util.Auth;
import com.hanul.caramelhomecchiato.util.KakaoApiUtils;
import com.hanul.caramelhomecchiato.util.SpinnerHandler;
import com.hanul.caramelhomecchiato.util.Validate;
import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.user.model.Account;
import com.kakao.sdk.user.model.Profile;

import java.io.Serializable;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JoinKakaoActivity extends AppCompatActivity{
	private static final String TAG = "JoinKakaoActivity";

	public static final String EXTRA_MODE = "mode";

	private final SpinnerHandler spinnerHandler = new SpinnerHandler(this);

	private View root;
	private EditText editTextName;
	private Button buttonJoin;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_join_kakao_with_name);

		Serializable _mode = getIntent().getSerializableExtra(EXTRA_MODE);
		if(!(_mode instanceof Mode)){
			throw new IllegalStateException("No mode specified for JoinKakaoActivity");
		}
		Mode mode = (Mode)_mode;

		root = findViewById(R.id.root);
		root.setVisibility(View.INVISIBLE);

		editTextName = findViewById(R.id.editTextName);
		buttonJoin = findViewById(R.id.buttonJoin);

		KakaoApiUtils.loginWithKakao(this, (oAuthToken, error) -> {
			if(error!=null){
				Log.e(TAG, "카카오톡 로그인 실패", error);
			}else if(oAuthToken!=null){
				spinnerHandler.show();

				Call<JsonObject> call;
				switch(mode){
				case LOGIN:
					call = KakaoIntegrationService.INSTANCE.loginWithKakao(oAuthToken.getAccessToken());
					break;
				case JOIN:
					call = KakaoIntegrationService.INSTANCE.joinWithKakao(oAuthToken.getAccessToken(), null);
					break;
				default:
					throw new IllegalStateException("Unreachable");
				}

				call.enqueue(new Callback<JsonObject>(){
					@Override public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response){
						kakaoLoginCallback(oAuthToken, response.body());
					}
					@Override public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t){
						Log.e(TAG, "loginWithKakao: Failure", t);
						Toast.makeText(JoinKakaoActivity.this, "예상치 못한 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
						setResult(RESULT_CANCELED);
						finish();
					}
				});
			}
		});

	}

	private void kakaoLoginCallback(OAuthToken oAuthToken, JsonObject jsonObject){
		if(jsonObject.has("error")){
			String loginError = jsonObject.get("error").getAsString();
			switch(loginError){
			case "needs_agreement": // 프로필 이용을 위한 동의가 필요
				fetchProfileAndJoin(oAuthToken, true);
				return;
			case "bad_kakao_login_token":
				Toast.makeText(this, "카카오 로그인이 해제되었습니다. 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
				break;
			case "user_exists":
				Toast.makeText(this, "동일한 계정의 유저가 이미 존재합니다.", Toast.LENGTH_SHORT).show();
				break;
			case "kakao_service_unavailable":
				Toast.makeText(this, "카카오 연동 서비스를 제공할 수 없습니다.", Toast.LENGTH_SHORT).show();
				break;
			default:
				Log.e(TAG, "kakaoLoginCallback: 예상치 못한 오류: "+loginError);
				Toast.makeText(this, "예상치 못한 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
				break;
			}
			setResult(RESULT_CANCELED);
			finish();
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
				setResult(RESULT_CANCELED);
				finish();
				return;
			}

			Account acc = user.getKakaoAccount();
			if(acc!=null){
				Profile profile = acc.getProfile();
				if(profile!=null){
					// 닉네임 확인, 이름 명시하여 재시도
					KakaoIntegrationService.INSTANCE.joinWithKakao(oAuthToken.getAccessToken(), profile.getNickname()).enqueue(new Callback<JsonObject>(){
						@Override public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response){
							JsonObject body = response.body();
							if(body.has("error")){
								Log.e(TAG, "joinWithKakao: 예상치 못한 오류: "+body.get("error").getAsString());
								Toast.makeText(JoinKakaoActivity.this, "예상치 못한 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
								setResult(RESULT_CANCELED);
								finish();
								return;
							}

							Auth.getInstance().setLoginData(body);
							setResult(RESULT_OK);
							finish();
						}
						@Override public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t){
							Log.e(TAG, "profile: Failure ", t);
							Toast.makeText(JoinKakaoActivity.this, "예상치 못한 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
							setResult(RESULT_CANCELED);
							finish();
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
			root.setVisibility(View.VISIBLE);
			buttonJoin.setOnClickListener(v -> joinWithName(oAuthToken));
		});
	}

	private void joinWithName(OAuthToken oAuthToken){
		String name = editTextName.getText().toString().trim();
		if(name.isEmpty()){
			Toast.makeText(this, "이름을 입력해주세요.", Toast.LENGTH_SHORT).show();
			return;
		}
		if(!Validate.name(name)){
			Toast.makeText(this, "유효하지 않은 이름입니다.", Toast.LENGTH_SHORT).show();
			return;
		}

		KakaoIntegrationService.INSTANCE.joinWithKakao(oAuthToken.getAccessToken(), name).enqueue(new Callback<JsonObject>(){
			@Override public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response){
				JsonObject body = response.body();
				if(body.has("error")){
					Toast.makeText(JoinKakaoActivity.this, "예상치 못한 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
					Log.e(TAG, "joinWithKakao: "+body.get("error").getAsString());
					setResult(RESULT_CANCELED);
					finish();
					return;
				}
				Auth.getInstance().setLoginData(body);
				finish();
			}
			@Override public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t){
				Toast.makeText(JoinKakaoActivity.this, "예상치 못한 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
				Log.e(TAG, "joinWithKakao: Failure", t);
				setResult(RESULT_CANCELED);
				finish();
			}
		});
	}

	public enum Mode{
		LOGIN,
		JOIN
	}
}