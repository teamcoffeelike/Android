package com.hanul.caramelhomecchiato;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.hanul.caramelhomecchiato.task.BaseTask;

import java.lang.ref.WeakReference;

/**
 * 앱의 시작 Activity.
 */
public class LoadingActivity extends AppCompatActivity{
	private static final String TAG = "LoadingActivity";

	private static final int LOGIN_REQ = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_loading);

		new PreLoginLoadingTask(this).execute();
	}

	@Override protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode==LOGIN_REQ){
			if(resultCode==RESULT_OK){
				int userId = data==null ? 0 : data.getIntExtra("userId", 0);
				if(userId==0){
					Log.e(TAG, "onActivityResult: 로그인 Activity가 OK를 반환하였으나 추가적인 데이터를 반환하지 않았습니다.");
					finish();
					return;
				}
				setUserIdAndPostLoad(userId);
			}else{ // 로그인 캔슬, 프로그램 종료
				finish();
			}
		}
	}

	private void setUserIdAndPostLoad(int userId){
		Log.d(TAG, "setUserIdAndPostLoad: userId = "+userId);
		// TODO Application에 userId 저장
		// TODO 모종의 이유로 로그아웃 시 다시 로그인?
		new PostLoginLoadingTask(this).execute();
	}

	/**
	 * 앱의 시작 후 바로 실행되는 Task입니다.<br>
	 *
	 */
	public static final class PreLoginLoadingTask extends BaseTask<LoadingActivity, Void, Void, Integer>{
		public PreLoginLoadingTask(LoadingActivity activity){
			super(activity);
		}

		@Override protected Integer doInBackground(Void... voids){
			try{
				Thread.sleep(3000);
			}catch(InterruptedException e){
				e.printStackTrace();
				Thread.currentThread().interrupt();
			}
			return null;
		}

		@Override protected void onPostExecute(@NonNull LoadingActivity activity, Integer userId){
			if(userId==null){
				activity.startActivityForResult(new Intent(activity, LoginActivity.class), LOGIN_REQ);
			}else{
				activity.setUserIdAndPostLoad(userId);
			}
		}
	}

	public static final class PostLoginLoadingTask extends BaseTask<LoadingActivity, Void, Void, Void>{
		public PostLoginLoadingTask(LoadingActivity activity){
			super(activity);
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

		@Override protected void onPostExecute(@NonNull LoadingActivity activity, Void aVoid){
			activity.startActivity(new Intent(activity, MainActivity.class));
			activity.finish();
		}
	}
}