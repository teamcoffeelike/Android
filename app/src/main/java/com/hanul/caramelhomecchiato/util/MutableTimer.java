package com.hanul.caramelhomecchiato.util;

import android.os.CountDownTimer;

import androidx.annotation.Nullable;

public class MutableTimer {
	private static final long TIME_INTERVAL = 100;

	private CountDownTimer timer;

	private long initialTime;
	private long mostRecentTime;

	@Nullable private OnTimeUpdatedListener onTimeUpdatedListener;

	public long getInitialTime() {
		return initialTime;
	}
	public void setInitialTime(long initialTime) {
		this.initialTime = initialTime;
		if(mostRecentTime==0) notifyTime();
	}

	public void setOnTimeUpdatedListener(@Nullable OnTimeUpdatedListener listener){
		onTimeUpdatedListener = listener;
	}

	public boolean isRunning(){
		return mostRecentTime!=0&&timer!=null;
	}

	public boolean isPaused(){
		return mostRecentTime!=0&&timer==null;
	}

	public void start(){
		start(initialTime);
	}

	public void resume(){
		start(mostRecentTime);
	}

	private void start(long userSettingTime){
		if(timer!=null) timer.cancel();
		timer = new CountDownTimer(userSettingTime, TIME_INTERVAL) {
			@Override
			public void onTick(long millisUntilFinished) {
				mostRecentTime = millisUntilFinished;
				notifyTime();
			}

			@Override
			public void onFinish() {
				stop();
			}
		};
		mostRecentTime = initialTime;
		notifyTime();
		timer.start();
	}

	public void stop(){
		if(timer!=null){
			timer.cancel();
			timer = null;
			mostRecentTime = 0;
			notifyTime();
		}
	}

	public void pause(){
		if (timer!=null){
			timer.cancel();
			timer = null;
		}
	}

	/*시간을 더했을때 멈추고 멈추었을때 시간을 저장하고있어야하고 멈춘시간+시간더하기 하고 다시 시작하는 메소드...^^...*/
	public void addTime(long time) {
		if(isRunning()){
			pause();
			mostRecentTime += time;
			resume();
		}else if(isPaused()){
			mostRecentTime += time;
			notifyTime();
		}else{
			setInitialTime(initialTime+time);
		}
	}

	private void notifyTime(){
		if(onTimeUpdatedListener!=null) onTimeUpdatedListener.onTimeUpdated(mostRecentTime==0 ? initialTime : mostRecentTime);
	}

	public interface OnTimeUpdatedListener {
		void onTimeUpdated(long t);
	}
}