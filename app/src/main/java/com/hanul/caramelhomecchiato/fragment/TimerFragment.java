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

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class TimerFragment extends Fragment{

	private Button tFiveMin, tOneMin, tSec, tStartPause, tStop;
	private TextView tCountDown;
	private ImageButton tReset;

	private CountDownTimer countDownTimer;
	private long timeLeftInMillis = 0L;
	private long userSettingTime = 0L;

	private boolean timeStarted = false;

	@Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.fragment_timer, container, false);

		tCountDown = view.findViewById(R.id.textTimerCountdown);

		//5분 추가 버튼
		tFiveMin = view.findViewById(R.id.buttonTimerFiveMin);
		tFiveMin.setOnClickListener(v -> {
			timeLeftInMillis += 300000;
			userSettingTime = timeLeftInMillis;
			updateCountDownText();
		});

		//1분 추가버튼
		tOneMin = view.findViewById(R.id.buttonTimerOneMin);
		tOneMin.setOnClickListener(v -> {
			timeLeftInMillis += 60000;
			userSettingTime = timeLeftInMillis;
			updateCountDownText();
		});

		//15초 추가버튼
		tSec = view.findViewById(R.id.buttonTimerSec);
		tSec.setOnClickListener(v -> {
			timeLeftInMillis += 15000;
			userSettingTime = timeLeftInMillis;
			updateCountDownText();
		});

		//시작/일시정지버튼
		tStartPause = view.findViewById(R.id.buttonTimerStartPause);
		tStartPause.setOnClickListener(v -> {
			if(timeLeftInMillis != 0) {
				if (timeStarted == false) {
					startTimer();
				} else {
					pauseTimer();
				}
			}
		});

		//정지버튼
		tStop = view.findViewById(R.id.buttonTimerStop);
		tStop.setOnClickListener(v -> {
			stopTimer();
		});

		//시간 리셋버튼
		tReset = view.findViewById(R.id.imgBtnReset);
		tReset.setOnClickListener(v -> {
			resetTime();
		});

		updateCountDownText();

		return view;
	}

	//타이머 시작
	private void startTimer() {
		countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
			@Override
			public void onTick(long millisUntilFinished) {
				timeLeftInMillis = millisUntilFinished;
				updateCountDownText();
			}
			//타이머가 완전히 끝나면
			@Override
			public void onFinish() {
				timeStarted = false;
				tStartPause.setText("시작");
				timerAlarm();
			}
		}.start();

		timeStarted = true;
		tStartPause.setText("일시정지");
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
		tStartPause.setText("계속");
	}

	//타이머 중지
	private void stopTimer() {
		countDownTimer.cancel();
		timeStarted = false;
		tStartPause.setText("시작");
		timeLeftInMillis = userSettingTime;
		updateCountDownText();
	}

	//시간 리셋
	private void resetTime() {
		countDownTimer.cancel();
		timeStarted = false;
		tStartPause.setText("시작");
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

		tCountDown.setText(timeLeftFormatted);
	}
}
