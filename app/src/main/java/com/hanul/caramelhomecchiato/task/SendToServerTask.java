package com.hanul.caramelhomecchiato.task;

import android.content.Context;
import android.net.http.AndroidHttpClient;

import com.hanul.caramelhomecchiato.util.NetUtils;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 서버로 메시지를 보내는 {@code AsyncTask}. 멀티파트 객체를 사용한 HTTP 연결의 기초를 구현합니다.
 */
public abstract class SendToServerTask<CONTEXT extends Context, Params, Progress, Result>
		extends BaseTask<CONTEXT, Params, Progress, Result>{
	private final String server;
	private final String subroutine;

	public SendToServerTask(CONTEXT context, String subroutine){
		this(context, NetUtils.API_ADDRESS, subroutine);
	}
	public SendToServerTask(CONTEXT context, String server, String subroutine){
		super(context);
		this.server = server;
		this.subroutine = subroutine;
	}

	@SafeVarargs @Override protected final Result doInBackground(Params... params){
		MultipartEntityBuilder builder = MultipartEntityBuilder.create()
				.setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
				.setCharset(StandardCharsets.UTF_8);

		appendMultipartEntity(builder);

		AndroidHttpClient client = AndroidHttpClient.newInstance("Android");
		try{
			HttpPost post = new HttpPost("http://"+server+"/"+subroutine);
			post.setEntity(builder.build());
			HttpResponse response = client.execute(post);
			return onReceiveResponse(response);
		}catch(IOException e){
			e.printStackTrace();
			cancel(false);
			return null;
		}finally{
			client.close();
		}
	}

	/**
	 * 전송할 데이터를 추가합니다.
	 *
	 * @param builder 멀티파트 객체
	 */
	protected abstract void appendMultipartEntity(MultipartEntityBuilder builder);

	/**
	 * 전송받은 데이터를 최종 결과로 변환시킵니다.
	 *
	 * @param response
	 */
	protected abstract Result onReceiveResponse(HttpResponse response) throws IOException;
}
