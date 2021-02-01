package com.hanul.caramelhomecchiato.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.hanul.caramelhomecchiato.R;
import com.hanul.caramelhomecchiato.util.MutableTimer;

import java.util.Locale;

public class TimerFragment extends Fragment{
	private Button buttonTimerFiveMin;
	private Button buttonTimerOneMin;
	private Button buttonTimerSec;
	private Button buttonTimerStartPause;
	private Button buttonTimerStop;
	private TextView textTimerCountdown;
	private ImageButton imgBtnReset;

	private final MutableTimer timer = new MutableTimer();

	@Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.fragment_timer, container, false);

		textTimerCountdown = view.findViewById(R.id.textTimerCountdown);
		buttonTimerFiveMin = view.findViewById(R.id.buttonTimerFiveMin);
		buttonTimerOneMin = view.findViewById(R.id.buttonTimerOneMin);
		buttonTimerSec = view.findViewById(R.id.buttonTimerSec);
		buttonTimerStartPause = view.findViewById(R.id.buttonTimerStartPause);
		buttonTimerStop = view.findViewById(R.id.buttonTimerStop);
		imgBtnReset = view.findViewById(R.id.imgBtnReset);

		buttonTimerFiveMin.setOnClickListener(v -> timer.addTime(300000));
		buttonTimerOneMin.setOnClickListener(v -> timer.addTime(60000));
		buttonTimerSec.setOnClickListener(v -> timer.addTime(15000));

		buttonTimerStartPause.setOnClickListener(v -> {
			if(timer.isRunning()) timer.pause();
			else if(timer.isPaused()) timer.resume();
			else timer.start();
		});

		buttonTimerStop.setOnClickListener(v -> timer.stop());

		imgBtnReset.setOnClickListener(v -> {
			timer.setInitialTime(0);
			timer.stop();
		});

		timer.setOnTimeUpdatedListener(new MutableTimer.OnTimeUpdatedListener(){
			@Override public void onStopped(long t){
				buttonTimerStartPause.setText("시작");
				updateCountDownText(t);
			}
			@Override public void onResumed(long t){
				buttonTimerStartPause.setText("일시정지");
				updateCountDownText(t);
			}
			@Override public void onPaused(long t){
				buttonTimerStartPause.setText("계속");
				updateCountDownText(t);
			}
		});

		updateCountDownText(0);

		return view;
	}

	private void updateCountDownText(long t){
		long seconds = t/1000;
		long minutes = seconds/60;

		textTimerCountdown.setText(
				minutes >= 60 ?
						String.format(Locale.ENGLISH, "%d:%02d:%02d", minutes/60, minutes%60, seconds%60) :
						String.format(Locale.ENGLISH, "%02d:%02d", minutes, seconds%60));
	}
}