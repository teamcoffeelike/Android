package com.hanul.caramelhomecchiato;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity{
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		findViewById(R.id.buttonLogin).setOnClickListener(v -> {
			startActivity(new Intent(this, MainActivity.class));
		});
		findViewById(R.id.buttonJoin).setOnClickListener(v -> {
			startActivity(new Intent(this, JoinActivity.class));
		});
	}
}