package com.hanul.caramelhomecchiato;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class EmailCheck extends AppCompatActivity implements View.OnClickListener {

	EditText emailCheckNumber;
	Button buttonSendEmailCode;

	LayoutInflater dialog;
	View emailCheckView;
	Dialog emailCheckDialog;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_email_check);

		emailCheckNumber = findViewById(R.id.emailCheckNumber);
		buttonSendEmailCode = findViewById(R.id.buttonSendEmailCode);
		buttonSendEmailCode.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.buttonSendEmailCode:
				dialog = LayoutInflater.from(this);
				emailCheckView = dialog.inflate(R.layout.view_email_check, null);
				emailCheckDialog = new Dialog(this);	//Dialog 객체 생성
				emailCheckDialog.setContentView(emailCheckView);	//Dialog에 inflate한 View 탑재
				emailCheckDialog.setCanceledOnTouchOutside(false);	//Dialog 바깥부분 선택해도 닫히지 않게 설정
				emailCheckDialog.show();
		}
	}
}
