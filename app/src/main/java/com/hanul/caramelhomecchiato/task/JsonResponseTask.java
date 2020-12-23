package com.hanul.caramelhomecchiato.task;

import com.google.gson.JsonObject;
import com.hanul.caramelhomecchiato.util.NetUtils;

import org.apache.http.HttpResponse;

import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 서버로 메시지를 보내고 결과로 Json 오브젝트를 전달받는 {@code AsyncTask}.
 */
public abstract class JsonResponseTask<CONTEXT> extends SendToServerTask<CONTEXT, Void, Void, JsonObject>{
	public JsonResponseTask(CONTEXT context, String subroutine){
		super(context, subroutine);
	}
	public JsonResponseTask(CONTEXT context, String server, String subroutine){
		super(context, server, subroutine);
	}

	@Override protected JsonObject onReceiveResponse(HttpResponse response) throws IOException{
		return NetUtils.GSON.fromJson(new InputStreamReader(response.getEntity().getContent()), JsonObject.class);
	}
}
