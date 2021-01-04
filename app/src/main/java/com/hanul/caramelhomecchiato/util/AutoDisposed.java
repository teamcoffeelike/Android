package com.hanul.caramelhomecchiato.util;

import android.app.ProgressDialog;
import android.content.Context;

import androidx.activity.ComponentActivity;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

import java.util.Objects;

public abstract class AutoDisposed implements LifecycleEventObserver{
	protected final Context context;

	public AutoDisposed(ComponentActivity activity){
		this((Context)activity);
		activity.getLifecycle().addObserver(this);
	}
	public AutoDisposed(Fragment fragment){
		this(Objects.requireNonNull(fragment.getContext()));
		fragment.getLifecycle().addObserver(this);
	}

	private AutoDisposed(Context context){
		this.context = context;
	}

	@Override public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event){
		if(event==Lifecycle.Event.ON_DESTROY) onDestroy();
	}

	protected abstract void onDestroy();
}
