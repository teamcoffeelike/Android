package com.hanul.caramelhomecchiato;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SettingsActivity extends AppCompatActivity{

	private Button btnChangePw;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

		btnChangePw = findViewById(R.id.buttonChangePassword);
		btnChangePw.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), ChangePasswordActivity.class);
				startActivity(intent);
			}
		});

	}
}