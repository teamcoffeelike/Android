package com.hanul.caramelhomecchiato.task;

import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;

import com.hanul.caramelhomecchiato.common.CommonMethod;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

public class JoinPhoneTask extends AsyncTask<Void, Void, String> {

	// 데이터베이스에 삽입결과 0보다크면 삽입성공, 같거나 작으면 실패
	// 필수 부분
	String state = "";

	HttpClient httpClient;
	HttpPost httpPost;
	HttpResponse httpResponse;
	HttpEntity httpEntity;

	String phone;
	String name;
	String password;
	String pwconfirm;

	public JoinPhoneTask(String phone, String name, String password, String pwconfirm) {
		this.phone = phone;
		this.name = name;
		this.password = password;
		this.pwconfirm = pwconfirm;
	}

	@Override
	protected String doInBackground(Void... voids) {
		try {
			// MultipartEntityBuild 생성
			// 필수 부분
			MultipartEntityBuilder builder = MultipartEntityBuilder.create();
			builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
			builder.setCharset(Charset.forName("UTF-8"));

			// 문자열 및 데이터 추가
			builder.addTextBody("phone", phone, ContentType.create("Multipart/related", "UTF-8"));
			builder.addTextBody("name", name, ContentType.create("Multipart/related", "UTF-8"));
			builder.addTextBody("password", password, ContentType.create("Multipart/related", "UTF-8"));
			builder.addTextBody("pwconfirm", pwconfirm, ContentType.create("Multipart/related", "UTF-8"));

			String postURL = CommonMethod.ipConfig + "/coffeelike/joinWithPhone";

			// 전송
			InputStream inputStream = null;
			httpClient = AndroidHttpClient.newInstance("Android");
			httpPost = new HttpPost(postURL);
			httpPost.setEntity(builder.build());
			httpResponse = httpClient.execute(httpPost);    //여기 라인에서 DB에 보냄
			httpEntity = httpResponse.getEntity();
			inputStream = httpEntity.getContent();

			// 응답
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
			StringBuilder stringBuilder = new StringBuilder();
			String line = null;
			while ((line = bufferedReader.readLine()) != null){
				stringBuilder.append(line + "\n");
			}
			state = stringBuilder.toString();

			inputStream.close();

		}  catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(httpEntity != null){
				httpEntity = null;
			}
			if(httpResponse != null){
				httpResponse = null;
			}
			if(httpPost != null){
				httpPost = null;
			}
			if(httpClient != null){
				httpClient = null;
			}
		}
		return state;
	}
}

