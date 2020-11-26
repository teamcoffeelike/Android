package com.hanul.caramelhomecchiato;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.lang.ref.WeakReference;

/**
 * 앱의 시작 Activity.
 */
public class LoadingActivity extends AppCompatActivity{
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_loading);

		new LoadingTask(this).execute();
	}

	/**
	 * 앱의 시작 후 바로 실행되는 Task로, 서버와의 연결을 비롯한 작업을 수행합니다.
	 */
	public static final class LoadingTask extends AsyncTask<Void, Void, Void>{
		private final WeakReference<LoadingActivity> activity;

		public LoadingTask(LoadingActivity activity){
			this.activity = new WeakReference<>(activity);
		}

		@Override protected Void doInBackground(Void... voids){
			try{
				Thread.sleep(3000);
			}catch(InterruptedException e){
				e.printStackTrace();
				Thread.currentThread().interrupt();
			}
			return null;
		}

		@Override protected void onPostExecute(Void aVoid){
			LoadingActivity activity = this.activity.get();
			if(activity!=null){
				activity.startActivity(new Intent(activity, LoginActivity.class));
				activity.finish();
			}
		}
	}
}