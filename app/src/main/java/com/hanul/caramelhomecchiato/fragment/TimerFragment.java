package com.hanul.caramelhomecchiato.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.hanul.caramelhomecchiato.R;

public class TimerFragment extends Fragment{
	private Button tFiveMin, tOneMin, tSec, tStartPause, tStop;
	private TextView tCountDown;

	@Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.fragment_timer, container, false);

		tCountDown = view.findViewById(R.id.timer_countdown);
		tFiveMin = view.findViewById(R.id.timer_fivemin);
		tOneMin = view.findViewById(R.id.timer_onemin);
		tSec = view.findViewById(R.id.timer_sec);
		tStartPause = view.findViewById(R.id.timer_startpause);
		tStop = view.findViewById(R.id.timer_stop);

		return view;
	}
}
