package com.hanul.caramelhomecchiato.fragment;

import android.content.DialogInterface;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.hanul.caramelhomecchiato.R;
import com.hanul.caramelhomecchiato.util.MutableTimer;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class TimerFragment extends Fragment{

	private Button btnFiveMin;
	private Button btnOneMin;
	private Button btnSec;
	private Button btnStartPause;
	private Button btnStop;
	private TextView tvCountDown;
	private ImageButton ImgBtnReset;

	private final MutableTimer countDownTimer = new MutableTimer(timeLeftInMillis, 1000/4) {
		@Override
		public void onTick(long millisUntilFinished) {
			timeLeftInMillis = millisUntilFinished;
			updateCountDownText();
		}
		//타이머가 완전히 끝나면
		@Override
		public void onFinish() {
			timeStarted = false;
			btnStartPause.setText("시작");
			timerAlarm();
		}
	};

	private long timeLeftInMillis = 0L;
	private long userSettingTime = 0L;

	private boolean timeStarted = false;

	@Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.fragment_timer, container, false);

		tvCountDown = view.findViewById(R.id.textTimerCountdown);


		//5분 추가 버튼
		btnFiveMin = view.findViewById(R.id.buttonTimerFiveMin);
		btnFiveMin.setOnClickListener(v -> addTime(300000));

		//1분 추가버튼
		btnOneMin = view.findViewById(R.id.buttonTimerOneMin);
		btnOneMin.setOnClickListener(v -> addTime(60000));

		//15초 추가버튼
		btnSec = view.findViewById(R.id.buttonTimerSec);
		btnSec.setOnClickListener(v -> addTime(15000));

		//시작/일시정지버튼
		btnStartPause = view.findViewById(R.id.buttonTimerStartPause);
		btnStartPause.setOnClickListener(v -> {
			if(timeLeftInMillis != 0) {
				if (timeStarted == false) {
					startTimer();
				} else {
					pauseTimer();
				}
			}
		});

		//정지버튼
		btnStop = view.findViewById(R.id.buttonTimerStop);
		btnStop.setOnClickListener(v -> stopTimer());

		//시간 리셋버튼
		ImgBtnReset = view.findViewById(R.id.imgBtnReset);
		ImgBtnReset.setOnClickListener(v -> resetTime());

		updateCountDownText();

		return view;
	}

	private void addTime(long time){
		if(timeStarted){
			countDownTimer.addTime(time);
		}else{
			userSettingTime += time;
			updateCountDownText();
		}
	}

	//타이머 시작
	private void startTimer() {
		countDownTimer.start();

		timeStarted = true;
		btnStartPause.setText("일시정지");
	}

	//타이머알람
	private void timerAlarm() {
		Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		Ringtone ringtone = RingtoneManager.getRingtone(getContext(), uri);
		ringtone.play();
	}

	//타이머 일시정지
	private void pauseTimer() {
		countDownTimer.cancel();
		timeStarted = false;
		btnStartPause.setText("계속");
	}

	//타이머 중지
	private void stopTimer() {
		countDownTimer.cancel();
		timeStarted = false;
		btnStartPause.setText("시작");
		timeLeftInMillis = userSettingTime;
		updateCountDownText();
	}

	//시간 리셋
	private void resetTime() {
		countDownTimer.cancel();
		timeStarted = false;
		btnStartPause.setText("시작");
		timeLeftInMillis = 0;
		userSettingTime = 0;
		updateCountDownText();
	}

	//시간부분
	private void updateCountDownText() {
		int	minutes = (int) (timeLeftInMillis / 1000) / 60;
		int	seconds = (int) (timeLeftInMillis / 1000) % 60;

		String timeLeftFormatted = null;

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
