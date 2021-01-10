package com.hanul.caramelhomecchiato.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.hanul.caramelhomecchiato.R;
import com.hanul.caramelhomecchiato.util.Validate;

public class ChangePasswordActivity extends AppCompatActivity{
	private TextView textViewNewPasswordConfirm;
	private TextView textViewNewPasswordCheckConfirm;
	private EditText editTextPassword;
	private EditText editTextNewPassword;
	private EditText editTextNewPasswordCheck;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_password);

		textViewNewPasswordConfirm = findViewById(R.id.textViewNewPasswordConfirm);
		textViewNewPasswordCheckConfirm = findViewById(R.id.textViewNewPasswordCheckConfirm);
		editTextPassword = findViewById(R.id.editTextPassword);
		editTextNewPassword = findViewById(R.id.editTextNewPassword);
		editTextNewPasswordCheck = findViewById(R.id.editTextNewPasswordCheck);

		editTextPassword.addTextChangedListener(new TextWatcher(){
			@Override public void beforeTextChanged(CharSequence s, int start, int count, int after){}
			@Override public void onTextChanged(CharSequence s, int start, int before, int count){
				updateNewPasswordConfirm();
				updateNewPasswordCheckConfirm();
			}
			@Override public void afterTextChanged(Editable s){}
		});

		editTextNewPasswordCheck.addTextChangedListener(new TextWatcher(){
			@Override public void beforeTextChanged(CharSequence s, int start, int count, int after){}
			@Override public void onTextChanged(CharSequence s, int start, int before, int count){
				updateNewPasswordCheckConfirm();
			}
			@Override public void afterTextChanged(Editable s){}
		});

		findViewById(R.id.buttonSubmit).setOnClickListener(v -> {

		});
	}

	private void updateNewPasswordConfirm(){
		Editable newPassword = editTextNewPassword.getText();
		if(newPassword.length()==0){
			textViewNewPasswordConfirm.setVisibility(View.INVISIBLE);
		}else if(newPassword.equals(editTextPassword.getText())){
			textViewNewPasswordConfirm.setVisibility(View.VISIBLE);
			textViewNewPasswordConfirm.setText("기존 비밀번호와 새 비밀번호가 일치합니다.");
		}else if(Validate.password(newPassword)){
			textViewNewPasswordConfirm.setVisibility(View.INVISIBLE);
		}else{
			textViewNewPasswordConfirm.setVisibility(View.VISIBLE);
			textViewNewPasswordConfirm.setText("잘못된 비밀번호 양식입니다(3자 이상).");
		}
	}

	private void updateNewPasswordCheckConfirm(){
		Editable newPassword = editTextNewPassword.getText();
		Editable newPasswordCheck = editTextNewPasswordCheck.getText();

		if(newPassword.length()==0||newPasswordCheck.length()==0||newPassword.toString().equals(newPasswordCheck.toString())){
			textViewNewPasswordCheckConfirm.setVisibility(View.INVISIBLE);
		}else{
			textViewNewPasswordCheckConfirm.setVisibility(View.VISIBLE);
		}
	}
}