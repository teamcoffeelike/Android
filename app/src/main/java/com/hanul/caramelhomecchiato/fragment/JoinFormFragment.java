package com.hanul.caramelhomecchiato.fragment;

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

import com.hanul.caramelhomecchiato.task.JoinEmailTask;
import com.hanul.caramelhomecchiato.JoinActivity.JoinType;
import com.hanul.caramelhomecchiato.R;
import com.hanul.caramelhomecchiato.task.JoinPhoneTask;

import java.util.concurrent.ExecutionException;

public class JoinFormFragment extends Fragment{
	private static final String TAG = "main:JoinFormFragment";
	
	private String state;

	private EditText etEmailPhone;
	private EditText etName;
	private EditText etPassword;
	private EditText etPwConfirm;
	private TextView tvPwCheck;
	private ImageView imgEmailPhone;
	private Button btnConfirm;

	private JoinType type;


	@Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.fragment_join_form, container, false);
		etEmailPhone = view.findViewById(R.id.editTextEmailPhone);
		etName = view.findViewById(R.id.editTextName);
		etPassword = view.findViewById(R.id.editTextPassword);
		etPwConfirm = view.findViewById(R.id.editTextPasswordConfirm);
		tvPwCheck = view.findViewById(R.id.textViewPwCheck);
		imgEmailPhone = view.findViewById(R.id.imgEmailPhone);
		btnConfirm = view.findViewById(R.id.buttonConfirm);

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
				if(etPassword.getText().toString().length()==0 || etPwConfirm.getText().toString().length()==0){
					tvPwCheck.setVisibility(View.GONE);
				}
			}

			@Override
			public void afterTextChanged(Editable s) { }
		});

		etPassword.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String password=etPassword.getText().toString();
				String confirm=etPwConfirm.getText().toString();
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
			}

			@Override
			public void afterTextChanged(Editable s) { }
		});


		btnConfirm.setOnClickListener(v -> {
			String name = etName.getText().toString();
			String password = etPassword.getText().toString();
			String pwconfirm = etPwConfirm.getText().toString();

			switch (type){
				case WITH_PHONE:{
					String phone = etEmailPhone.getText().toString();


					JoinPhoneTask joinPhoneTask = new JoinPhoneTask(phone, name, password, pwconfirm);

					try {
						state = joinPhoneTask.execute().get().trim();   //.get() : 데이터가 도착하기 전에 조회하는 것을 방지
						Log.d(TAG, "onClick: " + state);
					} catch (ExecutionException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
					break;
				case WITH_EMAIL: {
					String email = etEmailPhone.getText().toString();

					JoinEmailTask joinEmailTask = new JoinEmailTask(email, name, password, pwconfirm);

					try {
						state = joinEmailTask.execute().get().trim();   //.get() : 데이터가 도착하기 전에 조회하는 것을 방지
						Log.d(TAG, "onClick: " + state);
					} catch (ExecutionException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
					break;
				default:
					throw new IllegalArgumentException("type");
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
		this.type = type;

		switch(type){
		case WITH_PHONE:
			etEmailPhone.setHint("핸드폰 번호");
			imgEmailPhone.setImageResource(R.drawable.ic_join_phone);
			break;
		case WITH_EMAIL:
			etEmailPhone.setHint("이메일");
			imgEmailPhone.setImageResource(R.drawable.ic_join_email);
			break;
		case WITH_KAKAO:
		default:
			throw new IllegalArgumentException("type");
		}
	}
}
