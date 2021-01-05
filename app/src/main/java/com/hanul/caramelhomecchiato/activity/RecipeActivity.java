package com.hanul.caramelhomecchiato.activity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.hanul.caramelhomecchiato.R;
import com.hanul.caramelhomecchiato.data.Recipe;
import com.hanul.caramelhomecchiato.fragment.RecipeCoverContentFragment;
import com.hanul.caramelhomecchiato.fragment.RecipeCoverImageFragment;

import java.util.ArrayList;
import java.util.List;

public class RecipeActivity extends AppCompatActivity{
	public static final String EXTRA_RECIPE = "recipe";

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recipe);

		Recipe recipe = getIntent().getParcelableExtra(EXTRA_RECIPE);
		if(recipe==null) throw new IllegalStateException("No recipe provided for RecipeActivity");

		ViewPager viewPagerImage = findViewById(R.id.viewPagerImage);
		ViewPager viewPagerContent = findViewById(R.id.viewPagerContent);

		viewPagerImage.addOnPageChangeListener(new ViewPagerSynchronizer(viewPagerImage, viewPagerContent));
		viewPagerContent.addOnPageChangeListener(new ViewPagerSynchronizer(viewPagerContent, viewPagerImage));

		// Generate fragments
		List<Fragment> images = new ArrayList<>();
		List<Fragment> contents = new ArrayList<>();

		images.add(RecipeCoverImageFragment.newInstance(recipe.getCover()));
		contents.add(RecipeCoverContentFragment.newInstance(recipe.getCover()));

		FragmentManager fm = getSupportFragmentManager();

		viewPagerImage.setAdapter(new PagerAdapter(images, fm));
		viewPagerImage.setAdapter(new PagerAdapter(contents, fm));
	}

	// https://stackoverflow.com/a/26513243/12224135
	private static final class ViewPagerSynchronizer implements ViewPager.OnPageChangeListener{
		private final ViewPager thisViewPager;
		private final ViewPager otherViewPager;

		private int scrollState = ViewPager.SCROLL_STATE_IDLE;

		public ViewPagerSynchronizer(ViewPager thisViewPager, ViewPager otherViewPager){
			this.thisViewPager = thisViewPager;
			this.otherViewPager = otherViewPager;
		}

		@Override public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels){
			if(scrollState==ViewPager.SCROLL_STATE_IDLE) return;
			otherViewPager.scrollTo(thisViewPager.getScrollX(), otherViewPager.getScrollY());
		}
		@Override public void onPageSelected(int position){ }
		@Override public void onPageScrollStateChanged(int state){
			scrollState = state;
			if(state==ViewPager.SCROLL_STATE_IDLE){
				otherViewPager.setCurrentItem(thisViewPager.getCurrentItem(), false);
			}
		}
	}

	private static final class PagerAdapter extends FragmentStatePagerAdapter{
		private final List<Fragment> fragments;

		public PagerAdapter(List<Fragment> fragments, @NonNull FragmentManager fm){
			super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
			this.fragments = fragments;
		}

		@NonNull @Override public Fragment getItem(int position){
			return fragments.get(position);
		}
		@Override public int getCount(){
			return fragments.size();
		}
	}
}