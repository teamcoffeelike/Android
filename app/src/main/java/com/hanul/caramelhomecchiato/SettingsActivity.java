package com.hanul.caramelhomecchiato;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity{

	private Button btnChangePw;
	private Switch switchComment, switchFollow, switchLike;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

		switchComment = findViewById(R.id.switchComment);
		switchFollow = findViewById(R.id.switchFollow);
		switchLike = findViewById(R.id.switchLike);
		btnChangePw = findViewById(R.id.buttonChangePassword);

		/*Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);*/

		/* 비밀번호 변경 버튼 -> 비밀번호 변경 화면으로 이동 */
		btnChangePw.setOnClickListener(v -> {
			Intent intent = new Intent(getApplicationContext(), ChangePasswordActivity.class);
			startActivity(intent);
		});

		/* 댓글 알림 설정 */
		switchComment.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					Toast.makeText(SettingsActivity.this, "댓글 알림 설정 ON", Toast.LENGTH_SHORT).show();
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
						//vibrator.vibrate(VibrationEffect.createOneShot(100, 50));
					}else {
						//vibrator.vibrate(1000);
					}
				}else {
					Toast.makeText(SettingsActivity.this, "댓글 알림 설정 OFF", Toast.LENGTH_SHORT).show();
				}
			}
		});

		/* 팔로우 알림 설정 */
		switchFollow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					Toast.makeText(SettingsActivity.this, "팔로우 알림 설정 ON", Toast.LENGTH_SHORT).show();
				}else {
					Toast.makeText(SettingsActivity.this, "팔로우 알림 설정 OFF", Toast.LENGTH_SHORT).show();
				}
			}
		});

		switchLike.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					Toast.makeText(SettingsActivity.this, "좋아요 알림 설정 ON", Toast.LENGTH_SHORT).show();
				}else {
					Toast.makeText(SettingsActivity.this, "좋아요요 알림설정 OFF", Toast.LENGTH_SHORT).show();
				}
			}
		});

	}
}