package com.hanul.caramelhomecchiato;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;
import com.hanul.caramelhomecchiato.network.KakaoIntegrationService;
import com.hanul.caramelhomecchiato.util.Auth;
import com.kakao.sdk.auth.model.OAuthToken;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JoinKakaoWithNameActivity extends AppCompatActivity{
	private static final String TAG = "JoinKakaoWithNameActivi";

	public static final String EXTRA_KAKAO_AUTO_TOKEN = "oAuthToken";

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_join_kakao_with_name);

		OAuthToken oAuthToken = getIntent().getParcelableExtra(EXTRA_KAKAO_AUTO_TOKEN);
		if(oAuthToken==null) throw new IllegalStateException("JoinKakaoWithNameActivity가 OAuthToken 전달 없이 생성되었습니다.");

		EditText editTextName = findViewById(R.id.editTextName);

		findViewById(R.id.buttonJoin).setOnClickListener(v -> {
			String name = editTextName.getText().toString().trim();
			if(name.isEmpty()){
				Toast.makeText(this, "이름을 입력해주세요.", Toast.LENGTH_SHORT).show();
				return;
			}

			KakaoIntegrationService.INSTANCE.joinWithKakao(oAuthToken.getAccessToken(), name).enqueue(new Callback<JsonObject>(){
				@Override public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response){
					JsonObject body = response.body();
					if(body.has("error")){
						Toast.makeText(JoinKakaoWithNameActivity.this, "예상치 못한 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
						Log.e(TAG, "joinWithKakao: "+body.get("error").getAsString());
						return;
					}
					Auth.getInstance().setLoginData(body);
					finish();
				}
				@Override public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t){
					Toast.makeText(JoinKakaoWithNameActivity.this, "예상치 못한 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
					Log.e(TAG, "joinWithKakao: Failure", t);
				}
			});
		});
	}
}