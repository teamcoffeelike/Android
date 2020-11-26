package com.hanul.caramelhomecchiato.widget;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.RequiresApi;

/**
 * https://stackoverflow.com/a/26575808
 */
public class SquareLayout extends FrameLayout{
	public SquareLayout(Context context){
		super(context);
	}
	public SquareLayout(Context context, AttributeSet attrs){
		super(context, attrs);
	}
	public SquareLayout(Context context, AttributeSet attrs, int defStyleAttr){
		super(context, attrs, defStyleAttr);
	}
	@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
	public SquareLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes){
		super(context, attrs, defStyleAttr, defStyleRes);
	}

	@Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
		//noinspection SuspiciousNameCombination
		super.onMeasure(widthMeasureSpec, widthMeasureSpec);
	}
}
