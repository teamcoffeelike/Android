package com.hanul.caramelhomecchiato.task;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;

import java.lang.ref.WeakReference;

/**
 * 기본적인 {@code AsyncTask}. Weak Reference를 사용하여 Context를 접근하는 패턴을 구현합니다.<br/>
 * Android의 context 누수로 인한 메모리 장애에 관한 자세한 사항은 <a href="https://android-developers.googleblog.com/2009/01/avoiding-memory-leaks.html">여기</a>를 참조하세요.
 * @param <CONTEXT>
 * @param <Params>
 * @param <Progress>
 * @param <Result>
 */
public abstract class BaseTask<CONTEXT extends Context, Params, Progress, Result> extends AsyncTask<Params, Progress, Result>{
	private final WeakReference<CONTEXT> ctx;

	public BaseTask(CONTEXT ctx){
		this.ctx = new WeakReference<>(ctx);
	}

	@Override protected void onPostExecute(Result result){
		CONTEXT context = ctx.get();
		if(context!=null) onPostExecute(context, result);
	}

	@Override protected void onCancelled(Result result){
		CONTEXT context = ctx.get();
		if(context!=null) onCancelled(context, result);
	}
	protected abstract void onPostExecute(@NonNull CONTEXT context, Result result);
	protected void onCancelled(@NonNull CONTEXT context, Result result){}
}
