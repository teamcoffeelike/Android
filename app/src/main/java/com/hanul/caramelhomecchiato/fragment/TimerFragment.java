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

	private Button btnFiveMin;
	private Button btnOneMin;
	private Button btnSec;
	private Button btnStartPause;
	private Button btnStop;
	private TextView tvCountDown;
	private ImageButton ImgBtnReset;

	private final MutableTimer timer = new MutableTimer();

	@Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.fragment_timer, container, false);

		tvCountDown = view.findViewById(R.id.textTimerCountdown);

		//5분 추가 버튼
		btnFiveMin = view.findViewById(R.id.buttonTimerFiveMin);
		btnFiveMin.setOnClickListener(v -> timer.addTime(300000));

		//1분 추가버튼
		btnOneMin = view.findViewById(R.id.buttonTimerOneMin);
		btnOneMin.setOnClickListener(v -> timer.addTime(60000));

		//15초 추가버튼
		btnSec = view.findViewById(R.id.buttonTimerSec);
		btnSec.setOnClickListener(v -> timer.addTime(15000));

		//시작/일시정지버튼
		btnStartPause = view.findViewById(R.id.buttonTimerStartPause);
		btnStartPause.setOnClickListener(v -> {
			if (timer.isRunning()) pauseTimer();
			else if(timer.isPaused()) resumeTimer();
			else startTimer();
		});

		//정지버튼
		btnStop = view.findViewById(R.id.buttonTimerStop);
		btnStop.setOnClickListener(v -> stopTimer());

		//시간 리셋버튼
		ImgBtnReset = view.findViewById(R.id.imgBtnReset);
		ImgBtnReset.setOnClickListener(v -> resetTime());

		timer.setOnTimeUpdatedListener(t -> updateCountDownText(t));

		updateCountDownText(0);

		return view;
	}

	private void setTime(long time){
		timer.setInitialTime(time);
	}

	//타이머 시작
	private void startTimer() {
		timer.start();
		btnStartPause.setText("일시정지");
	}

	private void resumeTimer() {
		timer.resume();
		btnStartPause.setText("일시정지");
	}

	//타이머 일시정지
	private void pauseTimer() {
		timer.pause();
		btnStartPause.setText("계속");
	}

	//타이머 중지
	private void stopTimer() {
		timer.stop();
		btnStartPause.setText("시작");
	}

	//시간 리셋
	private void resetTime() {
		timer.stop();
		btnStartPause.setText("시작");
		timer.setInitialTime(0);
	}

	//시간부분
	private void updateCountDownText(long t) {
		int	minutes = (int) (t / 1000) / 60;
		int	seconds = (int) (t / 1000) % 60;

		String timeLeftFormatted;

		if(seconds < 10) {
			if (minutes < 10 ) {
				timeLeftFormatted = String.format(Locale.getDefault(), "0" + minutes + ":0" + seconds, minutes, seconds);
			}else{
				timeLeftFormatted = String.format(Locale.getDefault(),  minutes + ":0" + seconds, minutes, seconds);
			}
		}else {
			if (minutes < 10) {
				timeLeftFormatted = String.format(Locale.getDefault(), "0" + minutes + ":" + seconds, minutes, seconds);
			}else {
				timeLeftFormatted = String.format(Locale.getDefault(),  minutes + ":" + seconds, minutes, seconds);
			}
		}

		tvCountDown.setText(timeLeftFormatted);
	}
}