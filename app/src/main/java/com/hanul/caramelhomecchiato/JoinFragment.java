package com.hanul.caramelhomecchiato;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.hanul.caramelhomecchiato.JoinActivity.JoinType;

public class JoinFragment extends Fragment{
	@Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.fragment_join, container, false);

		Button buttonJoinWithPhone = view.findViewById(R.id.buttonJoinWithPhone);
		Button buttonJoinWithEmail = view.findViewById(R.id.buttonJoinWithEmail);
		Button buttonJoinWithKakao = view.findViewById(R.id.buttonJoinWithKakao);

		buttonJoinWithPhone.setOnClickListener(v -> tryOpenJoinForm(JoinType.WITH_PHONE));
		buttonJoinWithEmail.setOnClickListener(v -> tryOpenJoinForm(JoinType.WITH_EMAIL));
		buttonJoinWithKakao.setOnClickListener(v -> tryOpenJoinForm(JoinType.WITH_KAKAO));
		return view;
	}

	private void tryOpenJoinForm(JoinType type){
		FragmentActivity activity = this.getActivity();
		if(activity instanceof JoinActivity){
			((JoinActivity) activity).openJoinForm(type);
		}
	}
}
