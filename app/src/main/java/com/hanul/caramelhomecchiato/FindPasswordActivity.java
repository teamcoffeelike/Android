package com.hanul.caramelhomecchiato;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class FindPasswordActivity extends AppCompatActivity {

	LinearLayout linearLayoutFindPw;
	Button buttonSendPhoneCode;
	Button buttonSendEmailCode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_find_password);

		linearLayoutFindPw = findViewById(R.id.linearLayoutFindPw);
		buttonSendPhoneCode = findViewById(R.id.buttonSendPhoneCode);
		buttonSendEmailCode = findViewById(R.id.buttonSendEmailCode);

		buttonSendPhoneCode.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();

			}
		});

		buttonSendEmailCode.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});

	}
}