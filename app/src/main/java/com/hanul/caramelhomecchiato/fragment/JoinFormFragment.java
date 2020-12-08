package com.hanul.caramelhomecchiato.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.hanul.caramelhomecchiato.ATask.JoinInsert;
import com.hanul.caramelhomecchiato.JoinActivity.JoinType;
import com.hanul.caramelhomecchiato.R;

import java.util.concurrent.ExecutionException;

public class JoinFormFragment extends Fragment{
	private static final String TAG = "main:JoinFormFragment";
	
	String state;

	private EditText etEmailPhone;
	private EditText etName;
	private EditText etPassword;
	private EditText etPwConfirm;
	private TextView tvPwCheck;
	private ImageView imgEmailPhone;
	private Button btnConfirm;
	private Button btnCancel;
	private Button btnCheck;

	@Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.fragment_join_form, container, false);
		etEmailPhone = view.findViewById(R.id.editTextEmailPhone);
		etName = view.findViewById(R.id.editTextName);
		etPassword = view.findViewById(R.id.editTextPassword);
		etPwConfirm = view.findViewById(R.id.editTextPasswordConfirm);
		tvPwCheck = view.findViewById(R.id.tvPasswordChk);
		imgEmailPhone = view.findViewById(R.id.imgEmailPhone);
		btnConfirm = view.findViewById(R.id.buttonConfirm);
		btnCancel = view.findViewById(R.id.buttonCancel);
		btnCheck = view.findViewById(R.id.buttonCheck);


		//비밀번호 두개 비교해서 일치
		//비밀번호 확인
		etPwConfirm.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String password=etPassword.getText().toString();
				String confirm=etPwConfirm.getText().toString();
				if(password.equals(confirm)){
					tvPwCheck.setVisibility(View.VISIBLE);
					tvPwCheck.setText("비밀번호가 일치합니다");
					tvPwCheck.setTextColor(getResources().getColor(R.color.ForestGreenTraditional));
				}else{
					tvPwCheck.setVisibility(View.VISIBLE);
					tvPwCheck.setText("비밀번호가 일치하지 않습니다!");
					tvPwCheck.setTextColor(getResources().getColor(R.color.red));
				}
				if(confirm.equals(password)){
					tvPwCheck.setVisibility(View.VISIBLE);
					tvPwCheck.setText("비밀번호가 일치합니다");
					tvPwCheck.setTextColor(getResources().getColor(R.color.ForestGreenTraditional));
				}else{
					tvPwCheck.setVisibility(View.VISIBLE);
					tvPwCheck.setText("비밀번호가 일치하지 않습니다!");
					tvPwCheck.setTextColor(getResources().getColor(R.color.red));
				}
				if(etPassword.getText().toString().length()==0 || etPwConfirm.getText().toString().length()==0){
					tvPwCheck.setVisibility(View.GONE);
				}
				if(etPwConfirm.getText().toString().length()==0 || etPassword.getText().toString().length()==0){
					tvPwCheck.setVisibility(View.GONE);
				}
			}
			@Override
			public void afterTextChanged(Editable s) { }
		});


		btnConfirm.setOnClickListener(v -> {
			String email = null;
			String phone = null;
			if (etEmailPhone.equals(JoinType.WITH_EMAIL)){
				email = etEmailPhone.getText().toString();
			}else if(etEmailPhone.equals(JoinType.WITH_PHONE)){
				phone = etEmailPhone.getText().toString();
			}
			String name = etName.getText().toString();
			String password = etPassword.getText().toString();
			String pwconfirm = etPwConfirm.getText().toString();

			JoinInsert joinInsert = new JoinInsert(email, phone, name, password, pwconfirm);

			try {
				state = joinInsert.execute().get().trim();   //.get() : 데이터가 도착하기 전에 조회하는 것을 방지
				Log.d(TAG, "onClick: " + state);
			} catch (ExecutionException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			if(state.equals("1")) {
				Log.d(TAG, "onClick: 삽입 성공");
			} else {
				Log.d(TAG, "onClick: 삽입 실패");
			}
		});

		return view;
	}

	public void setJoinType(JoinType type){
		switch(type){
		case WITH_PHONE:
			etEmailPhone.setHint("핸드폰 번호");
			imgEmailPhone.setImageResource(R.drawable.ic_join_phone);
			btnCheck.setText("본인확인");
			break;
		case WITH_EMAIL:
			etEmailPhone.setHint("이메일");
			imgEmailPhone.setImageResource(R.drawable.ic_join_email);
			btnCheck.setText("중복확인");
			break;
		case WITH_KAKAO:
		default:
			throw new IllegalArgumentException("type");
		}
	}
}
