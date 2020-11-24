package com.hanul.caramelhomecchiato;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.hanul.caramelhomecchiato.JoinActivity.JoinType;

public class JoinFormFragment extends Fragment{
	private EditText editTextEmailPhone;

	@Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.fragment_join_form, container, false);
		editTextEmailPhone = view.findViewById(R.id.editTextEmailPhone);
		return view;
	}

	public void setJoinType(JoinType type){
		switch(type){
		case WITH_PHONE:
			editTextEmailPhone.setHint("폰!!!");
			break;
		case WITH_EMAIL:
			editTextEmailPhone.setHint("이메일!!!");
			break;
		case WITH_KAKAO:
		default:
			throw new IllegalArgumentException("type");
		}
	}
}
