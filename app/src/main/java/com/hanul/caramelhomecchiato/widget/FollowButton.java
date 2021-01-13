package com.hanul.caramelhomecchiato.widget;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.button.MaterialButton;
import com.hanul.caramelhomecchiato.R;

public class FollowButton extends MaterialButton{
	private static final int[] STATE_FOLLOWING = {R.attr.state_following};

	private boolean mFollowing = false;

	public FollowButton(@NonNull Context context){
		super(context);
	}
	public FollowButton(@NonNull Context context, @Nullable AttributeSet attrs){
		super(context, attrs);
	}
	public FollowButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr){
		super(context, attrs, defStyleAttr);
	}

	public boolean isFollowing(){
		return mFollowing;
	}
	public void setFollowing(boolean following){
		this.mFollowing = following;
	}

	@Override protected int[] onCreateDrawableState(int extraSpace){
		int[] drawableState = super.onCreateDrawableState(extraSpace);
		if(mFollowing) mergeDrawableStates(drawableState, STATE_FOLLOWING);
		return drawableState;
	}
}
