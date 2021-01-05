package com.hanul.caramelhomecchiato.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.hanul.caramelhomecchiato.R;

public class ChangePasswordActivity extends AppCompatActivity {

	private ImageButton passwordSubmit;
	private TextView tvPassword, tvNewPassword, tvCheckPassword, tvPwConfirm, tvCheckPwConfirm;
	private EditText etPassword, etNewPassword, etCheckPassword;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_password);

		passwordSubmit = findViewById(R.id.passwordSubmit);
		tvPassword = findViewById(R.id.textViewPassword);
		tvNewPassword = findViewById(R.id.textViewNewPassword);
		tvCheckPassword = findViewById(R.id.textViewCheckPassword);
		tvPwConfirm = findViewById(R.id.textViewPwConfirm);
		tvCheckPwConfirm = findViewById(R.id.textViewCheckPwConfirm);
		etPassword = findViewById(R.id.editTextPassword);
		etNewPassword = findViewById(R.id.editTextNewPassword);
		etCheckPassword = findViewById(R.id.editTextCheckPassword);

		/* TODO 현재 비밀번호 일치 여부 확인 */
		etPassword.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});

		/* 새로운 비밀번호 일치 여부 확인 */
		etCheckPassword.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String newPw = etNewPassword.getText().toString();
				String check = etCheckPassword.getText().toString();
				if (!newPw.equals(check)) {
					tvCheckPwConfirm.setVisibility(View.VISIBLE);
					tvCheckPwConfirm.setText("비밀번호가 일치하지 않습니다!");
					tvCheckPwConfirm.setTextColor(getResources().getColor(R.color.TerraCotta));
				}else {
					tvCheckPwConfirm.setVisibility(View.GONE);
				}
				if (newPw.length() == 0 || check.length() == 0) {
					tvCheckPwConfirm.setVisibility(View.GONE);
				}
			}

			@Override
			public void afterTextChanged(Editable s) { }
		});

	}
}