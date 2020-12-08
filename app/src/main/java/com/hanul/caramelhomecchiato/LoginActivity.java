package com.hanul.caramelhomecchiato;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class LoginActivity extends AppCompatActivity{
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		findViewById(R.id.buttonLogin).setOnClickListener(v -> {
			startActivity(new Intent(this, MainActivity.class));
			finish();
		});
		findViewById(R.id.buttonJoin).setOnClickListener(v -> {
			startActivity(new Intent(this, JoinActivity.class));
		});

		findViewById(R.id.textViewFindLoginIdPw).setOnClickListener(v -> {
			startActivity(new Intent(this, FindLoginIdPwActivity.class));
		});
	}
}