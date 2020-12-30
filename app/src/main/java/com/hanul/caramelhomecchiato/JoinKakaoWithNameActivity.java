package com.hanul.caramelhomecchiato;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.hanul.caramelhomecchiato.task.JoinWithKakaoTask;
import com.hanul.caramelhomecchiato.util.Auth;
import com.kakao.sdk.auth.model.OAuthToken;

public class JoinKakaoWithNameActivity extends AppCompatActivity{
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

			new JoinWithKakaoTask<>(this, oAuthToken, name)
					.onSucceed((activity, result) -> {
						Auth.getInstance().setLoginData(result);
						activity.finish();
					}).onCancelled((activity, result) -> {
			}).execute();
		});
	}
}