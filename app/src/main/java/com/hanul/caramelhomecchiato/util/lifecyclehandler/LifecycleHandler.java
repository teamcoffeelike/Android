package com.hanul.caramelhomecchiato.util.lifecyclehandler;

import android.content.Context;

import androidx.activity.ComponentActivity;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

import java.util.Objects;

public abstract class LifecycleHandler implements LifecycleEventObserver{
	@Nullable private Context context;
	@Nullable private final Fragment fragment;

	public LifecycleHandler(ComponentActivity activity){
		this(activity, activity, null);
	}
	public LifecycleHandler(Fragment fragment){
		this(null, fragment, fragment);
	}
	public LifecycleHandler(Context context, LifecycleOwner lifecycleOwner){
		this(context, lifecycleOwner, null);
	}
	private LifecycleHandler(@Nullable Context context, LifecycleOwner lifecycleOwner, @Nullable Fragment fragment){
		this.context = context;
		this.fragment = fragment;

		lifecycleOwner.getLifecycle().addObserver(this);
	}

	public Context getContext(){
		if(context==null){
			if(fragment!=null){
				context = fragment.requireContext();
			}else{
				throw new IllegalStateException("Context 제공 불가능");
			}
		}
		return context;
	}

	@Override public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event){
		if(event==Lifecycle.Event.ON_DESTROY) onDestroy();
	}

	protected abstract void onDestroy();
}
