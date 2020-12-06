package com.hanul.caramelhomecchiato.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

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

	private EditText editTextEmailPhone;
	private EditText editTextName;
	private EditText editTextPassword;
	private EditText editTextPasswordConfirm;
	private ImageView imgEmailPhone;
	private Button btnConfirm;

	@Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.fragment_join_form, container, false);
		editTextEmailPhone = view.findViewById(R.id.editTextEmailPhone);
		editTextName = view.findViewById(R.id.editTextName);
		editTextPassword = view.findViewById(R.id.editTextPassword);
		editTextPasswordConfirm = view.findViewById(R.id.editTextPasswordConfirm);
		imgEmailPhone = view.findViewById(R.id.imgEmailPhone);
		btnConfirm = view.findViewById(R.id.buttonConfirm);

		btnConfirm.setOnClickListener(v -> {
			String email = null;
			String phone = null;
			if (editTextEmailPhone.equals(JoinType.WITH_EMAIL)){
				email = editTextEmailPhone.getText().toString();
			}else if(editTextEmailPhone.equals(JoinType.WITH_PHONE)){
				phone = editTextEmailPhone.getText().toString();
			}
			String name = editTextName.getText().toString();
			String password = editTextPassword.getText().toString();
			String pwconfirm = editTextPasswordConfirm.getText().toString();

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
			editTextEmailPhone.setHint("핸드폰 번호");
			imgEmailPhone.setImageResource(R.drawable.ic_join_phone);
			break;
		case WITH_EMAIL:
			editTextEmailPhone.setHint("이메일");
			imgEmailPhone.setImageResource(R.drawable.ic_join_email);
			break;
		case WITH_KAKAO:
		default:
			throw new IllegalArgumentException("type");
		}
	}
}
