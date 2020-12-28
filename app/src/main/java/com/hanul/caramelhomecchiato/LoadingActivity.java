package com.hanul.caramelhomecchiato;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hanul.caramelhomecchiato.data.Post;
import com.hanul.caramelhomecchiato.data.UserProfile;
import com.hanul.caramelhomecchiato.task.BaseTask;
import com.hanul.caramelhomecchiato.task.GetProfileTask;
import com.hanul.caramelhomecchiato.task.GetRecentPostTask;
import com.hanul.caramelhomecchiato.task.LoginWithAuthTokenTask;
import com.hanul.caramelhomecchiato.util.Auth;
import com.hanul.caramelhomecchiato.util.NetUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

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

		new PreLoginLoadingTask(this)
				.onSucceed((activity, aVoid) -> {
					if(Auth.getInstance().getAuthToken()==null){
						activity.startActivityForResult(new Intent(activity, LoginActivity.class), LOGIN_REQ);
					}else{
						activity.postLoad();
					}
				})
				.onCancelled((activity, integer) -> {
					activity.startActivityForResult(new Intent(activity, LoginActivity.class), LOGIN_REQ);
				}).execute();
	}

	@Override protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode==LOGIN_REQ){
			if(resultCode==RESULT_OK){
				if(Auth.getInstance().getAuthToken()==null){
					throw new IllegalStateException("로그인 Activity가 추가적인 데이터 처리 없이 OK를 반환했습니다.");
				}
				postLoad();
			}else{ // 로그인 캔슬, 프로그램 종료
				finish();
			}
		}
	}

	private void postLoad(){
		// 아니 씨발 이게뭐에요 씨발 내 코틀린 돌려놔 씨발 씨발
		new GetProfileTask<>(this, Auth.getInstance().getLoginUser())
				.onSucceed((a1, o1) -> {
					if(o1.has("error")){
						Log.e(TAG, "postLoad: GetProfileTask 오류: "+o1.get("error").getAsString());
						a1.finish();
						return;
					}
					UserProfile profile = NetUtils.GSON.fromJson(o1, UserProfile.class);
					new GetRecentPostTask<>(a1)
							.onSucceed((a2, o2) -> {
								if(o2.has("error")){
									Log.e(TAG, "postLoad: GetProfileTask 오류: "+o2.get("error").getAsString());
									a2.finish();
									return;
								}
								List<Post> posts = new ArrayList<>();
								for(JsonElement e : o2.get("posts").getAsJsonArray()){
									posts.add(NetUtils.GSON.fromJson(e, Post.class));
								}
								a2.startActivity(new Intent(a2, MainActivity.class)
										.putExtra(MainActivity.EXTRA_PROFILE, profile)
										.putExtra(MainActivity.EXTRA_RECENT_POSTS, posts.toArray(new Post[0])));
								a2.finish();
							})
							.onCancelled((a2, e2) -> {
								Log.e(TAG, "postLoad: GetProfileTask 오류: "+e2);
								a2.finish();
							}).execute();
				})
				.onCancelled((a1, e1) -> {
					Log.e(TAG, "postLoad: GetProfileTask 오류: "+e1);
					a1.finish();
				}).execute();
	}

	/**
	 * 앱의 시작 후 바로 실행되는 Task입니다.<br>
	 */
	public static final class PreLoginLoadingTask extends BaseTask<LoadingActivity, Void, Void, Void>{
		private static final String TAG = "PreLoginLoadingTask";

		public PreLoginLoadingTask(LoadingActivity activity){
			super(activity);
		}

		@Override protected Void doInBackground(Void... voids){
			String authToken = Auth.getInstance().getAuthToken();
			if(authToken!=null){
				Log.d(TAG, "doInBackground: AuthToken("+authToken+") 발견. 로그인 시도.");
				try{
					JsonObject jsonObject = new LoginWithAuthTokenTask<>(this, authToken).get();
					if(jsonObject.has("error")){
						Log.d(TAG, "doInBackground: AuthToken 로그인 실패: "+jsonObject.get("error").getAsString());
					}else{
						Auth.getInstance().setLoginData(jsonObject);
					}
				}catch(ExecutionException|InterruptedException e){
					Log.e(TAG, "doInBackground: ", e);
					cancel(false);
				}
			}
			return null;
		}
	}
}