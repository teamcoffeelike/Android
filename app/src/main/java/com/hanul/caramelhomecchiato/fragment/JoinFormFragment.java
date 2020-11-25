package com.hanul.caramelhomecchiato.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.hanul.caramelhomecchiato.JoinActivity.JoinType;
import com.hanul.caramelhomecchiato.R;

public class JoinFormFragment extends Fragment{
	private EditText editTextEmailPhone;
	private ImageView imgEmailPhone;

	@Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.fragment_join_form, container, false);
		editTextEmailPhone = view.findViewById(R.id.editTextEmailPhone);
		imgEmailPhone = view.findViewById(R.id.imgEmailPhone);

		return view;
	}

	public void setJoinType(JoinType type){
		switch(type){
		case WITH_PHONE:
			editTextEmailPhone.setHint("폰!!!");
			imgEmailPhone.setImageResource(R.drawable.ic_join_phone);
			break;
		case WITH_EMAIL:
			editTextEmailPhone.setHint("이메일!!!");
			imgEmailPhone.setImageResource(R.drawable.ic_join_email);
			break;
		case WITH_KAKAO:
		default:
			throw new IllegalArgumentException("type");
		}
	}
}
