package com.hanul.caramelhomecchiato.task;

import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;

/**
 * 기본적인 {@code AsyncTask}. Weak Reference를 사용하여 Context를 접근하는 패턴을 구현합니다.<br/>
 * Android의 context 누수로 인한 메모리 장애에 관한 자세한 사항은 <a href="https://android-developers.googleblog.com/2009/01/avoiding-memory-leaks.html">여기</a>를 참조하세요.
 *
 * @param <CONTEXT>
 * @param <Params>
 * @param <Progress>
 * @param <Result>
 */
public abstract class BaseTask<CONTEXT, Params, Progress, Result> extends AsyncTask<Params, Progress, Result>{
	private final WeakReference<CONTEXT> ctx;
	@Nullable private BaseTaskCallback<CONTEXT, Result> onSucceed;
	@Nullable private BaseTaskCallback<CONTEXT, Result> onCancelled;

	public BaseTask(CONTEXT ctx){
		this.ctx = new WeakReference<>(ctx);
	}

	public BaseTask<CONTEXT, Params, Progress, Result> onSucceed(BaseTaskCallback<CONTEXT, Result> onSucceed){
		this.onSucceed = onSucceed;
		return this;
	}

	public BaseTask<CONTEXT, Params, Progress, Result> onCancelled(BaseTaskCallback<CONTEXT, Result> onCancelled){
		this.onCancelled = onCancelled;
		return this;
	}

	@Override protected void onPostExecute(Result result){
		if(onSucceed!=null){
			CONTEXT context = ctx.get();
			if(context!=null) onSucceed.onFinish(context, result);
		}
	}

	@Override protected void onCancelled(Result result){
		if(onCancelled!=null){
			CONTEXT context = ctx.get();
			if(context!=null) onCancelled.onFinish(context, result);
		}
	}

	public interface BaseTaskCallback<CONTEXT, Result>{
		void onFinish(@NonNull CONTEXT context, Result result);
	}
}
