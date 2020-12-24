package com.hanul.caramelhomecchiato.task;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.google.gson.JsonObject;
import com.hanul.caramelhomecchiato.util.NetUtils;

import org.apache.http.HttpResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

/**
 * 서버로 메시지를 보내고 결과로 Json 오브젝트를 전달받는 {@code AsyncTask}.
 */
public abstract class JsonResponseTask<CONTEXT> extends SendToServerTask<CONTEXT, Void, Void, JsonObject>{
	private static final String TAG = "JsonResponseTask";

	public JsonResponseTask(CONTEXT context, String subroutine){
		super(context, subroutine);
	}
	public JsonResponseTask(CONTEXT context, String server, String subroutine){
		super(context, server, subroutine);
	}

	@RequiresApi(api = Build.VERSION_CODES.N)
	@Override
	protected JsonObject onReceiveResponse(HttpResponse response) throws IOException{
		try(InputStream is = response.getEntity().getContent();
		    InputStreamReader isr = new InputStreamReader(is);
		    BufferedReader r = new BufferedReader(isr)){
			final String collect = r.lines().collect(Collectors.joining("\n"));

			Log.d(TAG, "onReceiveResponse: "+collect);
			return NetUtils.GSON.fromJson(collect, JsonObject.class);
		}
	}
}
