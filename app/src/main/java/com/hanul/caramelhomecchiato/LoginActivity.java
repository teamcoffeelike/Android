package com.hanul.caramelhomecchiato;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;
import com.hanul.caramelhomecchiato.network.LoginService;
import com.hanul.caramelhomecchiato.util.Auth;
import com.hanul.caramelhomecchiato.util.SpinnerHandler;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity{
	private static final String TAG = "LoginActivity";

	private static final int LOGIN_RESULT = 1;

	private final SpinnerHandler spinnerHandler = new SpinnerHandler(this);

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

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

			spinnerHandler.show();

			Call<JsonObject> call = isIdEmail ?
					LoginService.INSTANCE.loginWithEmail(id, pw) :
					LoginService.INSTANCE.loginWithPhoneNumber(id, pw);
			call.enqueue(new Callback<JsonObject>(){
				@Override public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response){
					loginCallback(response.body());
				}
				@Override public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t){
					spinnerHandler.dismiss();
				}
			});
		});

		findViewById(R.id.buttonJoin).setOnClickListener(v -> {
			startActivityForResult(new Intent(this, JoinActivity.class), LOGIN_RESULT);
		});

		findViewById(R.id.textViewFindPassword).setOnClickListener(v -> {
			startActivity(new Intent(this, FindPasswordActivity.class));
		});

		findViewById(R.id.buttonLoginWithKakao).setOnClickListener(v -> {
			startActivityForResult(new Intent(this, JoinKakaoActivity.class)
					.putExtra(JoinKakaoActivity.EXTRA_MODE, JoinKakaoActivity.Mode.LOGIN), LOGIN_RESULT);
		});
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
			spinnerHandler.dismiss();
			return;
		}

		Auth.getInstance().setLoginData(jsonObject);
		setResult(RESULT_OK);
		finish();
	}
}