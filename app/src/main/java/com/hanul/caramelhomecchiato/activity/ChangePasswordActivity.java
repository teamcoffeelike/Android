package com.hanul.caramelhomecchiato.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;
import com.hanul.caramelhomecchiato.R;
import com.hanul.caramelhomecchiato.network.UserService;
import com.hanul.caramelhomecchiato.util.BaseCallback;
import com.hanul.caramelhomecchiato.util.Validate;

import retrofit2.Call;
import retrofit2.Response;

public class ChangePasswordActivity extends AppCompatActivity{
	private static final String TAG = "ChangePasswordActivity";

	private TextView textViewPasswordConfirm;
	private TextView textViewNewPasswordConfirm;
	private TextView textViewNewPasswordCheckConfirm;
	private EditText editTextPassword;
	private EditText editTextNewPassword;
	private EditText editTextNewPasswordCheck;

	boolean passwordConfirm;
	boolean newPasswordConfirm;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_password);

		textViewPasswordConfirm = findViewById(R.id.textViewPasswordConfirm);
		textViewNewPasswordConfirm = findViewById(R.id.textViewNewPasswordConfirm);
		textViewNewPasswordCheckConfirm = findViewById(R.id.textViewNewPasswordCheckConfirm);
		editTextPassword = findViewById(R.id.editTextPassword);
		editTextNewPassword = findViewById(R.id.editTextNewPassword);
		editTextNewPasswordCheck = findViewById(R.id.editTextNewPasswordCheck);

		editTextNewPassword.addTextChangedListener(new TextWatcher(){
			@Override public void beforeTextChanged(CharSequence s, int start, int count, int after){}
			@Override public void onTextChanged(CharSequence s, int start, int before, int count){
				updateNewPasswordConfirm();
			}
			@Override public void afterTextChanged(Editable s){}
		});

		editTextNewPasswordCheck.addTextChangedListener(new TextWatcher(){
			@Override public void beforeTextChanged(CharSequence s, int start, int count, int after){}
			@Override public void onTextChanged(CharSequence s, int start, int before, int count){
				updateNewPasswordConfirm();
			}
			@Override public void afterTextChanged(Editable s){}
		});

		//저장 처리
		findViewById(R.id.buttonSubmit).setOnClickListener(v -> {
			Editable password = editTextPassword.getText();
			Editable newPassword = editTextNewPassword.getText();

			UserService.INSTANCE.setPassword(password.toString(), newPassword.toString()).enqueue(new BaseCallback(){
				@Override public void onSuccessfulResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response, @NonNull JsonObject result){
					if(passwordConfirm==true&&newPasswordConfirm==true){
						Toast.makeText(ChangePasswordActivity.this, "비밀번호가 변경되었습니다.", Toast.LENGTH_SHORT).show();
						finish();
					}
				}
				@Override public void onErrorResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response, @NonNull String error){
					Log.e(TAG, "changePassword: 예상치 못한 오류: "+error);
					switch(error){
						case "incorrect_password":
							Toast.makeText(ChangePasswordActivity.this, "현재 비밀번호가 일치하지 않습니다!", Toast.LENGTH_SHORT).show();
							break;
						case "bad_new_password":
							Toast.makeText(ChangePasswordActivity.this, "새 비밀번호가 현재 비밀번호와 일치하거나 비밀번호 양식이 올바르지 않습니다!", Toast.LENGTH_LONG).show();
							break;
						default:
							Toast.makeText(ChangePasswordActivity.this, "비밀번호를 다시 확인해주세요!", Toast.LENGTH_SHORT).show();
					}
				}
				@Override public void onFailedResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response){
					Log.e(TAG, "changePassword: 요청 실패 ");
					Toast.makeText(ChangePasswordActivity.this, "예상치 못한 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
				}
				@Override public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t){
					Log.e(TAG, "changePassword: Failure ", t);
					Toast.makeText(ChangePasswordActivity.this, "예상치 못한 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
				}
			});

		});
	}

	//유효성 검사
	private void updateNewPasswordConfirm(){
		Editable password = editTextPassword.getText();
		Editable newPassword = editTextNewPassword.getText();
		Editable newPasswordCheck = editTextNewPasswordCheck.getText();

		if(newPassword.toString().equals(password.toString())){
			textViewNewPasswordConfirm.setVisibility(View.VISIBLE);
			textViewNewPasswordConfirm.setText("현재 비밀번호와 동일합니다. 변경해주세요!");
			passwordConfirm = false;
			newPasswordConfirm = false;
		}else if(!Validate.password(newPassword.toString())){
			textViewNewPasswordConfirm.setVisibility(View.VISIBLE);
			textViewNewPasswordConfirm.setText("잘못된 비밀번호 양식입니다(3자 이상).");
			newPasswordConfirm = false;
		}else if(Validate.password(newPassword.toString())||newPassword.toString() != password.toString()){
			textViewNewPasswordConfirm.setVisibility(View.INVISIBLE);
			passwordConfirm = true;
		}

		if(newPassword.length()==0||newPasswordCheck.length()==0){
			textViewNewPasswordCheckConfirm.setVisibility(View.INVISIBLE);
			newPasswordConfirm = false;
		}else if(newPassword.toString().equals(newPasswordCheck.toString())){
			textViewNewPasswordCheckConfirm.setVisibility(View.INVISIBLE);
			newPasswordConfirm = true;
		}else{
			textViewNewPasswordCheckConfirm.setVisibility(View.VISIBLE);
			newPasswordConfirm = false;
		}

		Log.d("passwordConfirm", "현재 비밀번호 체크 상태 : " + passwordConfirm);
		Log.d("newPasswordConfirm", "새 비밀번호 체크 상태 : " + newPasswordConfirm);
	}
}