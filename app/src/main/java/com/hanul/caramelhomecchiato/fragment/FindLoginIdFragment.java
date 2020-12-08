package com.hanul.caramelhomecchiato.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.hanul.caramelhomecchiato.R;

public class FindLoginIdFragment extends Fragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.activity_find_login_id, container, false);
	}
}
