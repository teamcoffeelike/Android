package com.hanul.caramelhomecchiato;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import java.lang.reflect.Field;

/**
 * 스와이핑 동작 없는 ViewPager. 코드로 컨트롤 가능.
 * https://stackoverflow.com/a/9650884
 */
public class NoSwipeViewPager extends ViewPager{
	public NoSwipeViewPager(@NonNull Context context){
		super(context);
		setMyScroller();
	}
	public NoSwipeViewPager(@NonNull Context context, @Nullable AttributeSet attrs){
		super(context, attrs);
		setMyScroller();
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event){
		// Never allow swiping to switch between pages
		return false;
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event){
		// Never allow swiping to switch between pages
		return false;
	}

	//down one is added for smooth scrolling

	private void setMyScroller(){
		try{
			Class<?> viewpager = ViewPager.class;
			Field scroller = viewpager.getDeclaredField("mScroller");
			scroller.setAccessible(true);
			scroller.set(this, new MyScroller(getContext()));
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public static final class MyScroller extends Scroller{
		public MyScroller(Context context){
			super(context, new DecelerateInterpolator());
		}

		@Override
		public void startScroll(int startX, int startY, int dx, int dy, int duration){
			super.startScroll(startX, startY, dx, dy, 350 /*1 secs*/);
		}
	}
}
