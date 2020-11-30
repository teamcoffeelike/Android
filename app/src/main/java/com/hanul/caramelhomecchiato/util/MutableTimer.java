package com.hanul.caramelhomecchiato.util;

import android.os.CountDownTimer;

public abstract class MutableTimer extends CountDownTimer {

	/**
	 * @param millisInFuture    The number of millis in the future from the call
	 *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
	 *                          is called.
	 * @param countDownInterval The interval along the way to receive
	 *                          {@link #onTick(long)} callbacks.
	 */
	public MutableTimer(long millisInFuture, long countDownInterval) {
		super(millisInFuture, countDownInterval);
	}

	/*시간을 더했을때 멈추고 멈추었을때 시간을 저장하고있어야하고 멈춘시간+시간더하기 하고 다시 시작하는 메소드...^^...*/
	public void addTime(long time) {

	}
}