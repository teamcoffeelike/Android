package com.hanul.caramelhomecchiato.activity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.hanul.caramelhomecchiato.R;
import com.hanul.caramelhomecchiato.fragment.FollowerFragment;
import com.hanul.caramelhomecchiato.fragment.FollowingFragment;
import com.hanul.caramelhomecchiato.util.Auth;

public class FollowsActivity extends AppCompatActivity{
	private FollowerFragment followerFragment;
	private FollowingFragment followingFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_follows);

		int loginUser = Auth.getInstance().expectLoginUser();
		followerFragment = FollowerFragment.newInstance(loginUser);
		followingFragment = FollowingFragment.newInstance(loginUser);

		ViewPager viewPager = findViewById(R.id.viewPager);

		viewPager.setAdapter(new ViewPagerAdapter());

		TabLayout tabLayout = findViewById(R.id.tabLayout);
		tabLayout.setupWithViewPager(viewPager);
	}


	private final class ViewPagerAdapter extends FragmentStatePagerAdapter{
		public ViewPagerAdapter(){
			super(getSupportFragmentManager(), BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
		}

		@Override public CharSequence getPageTitle(int position){
			switch(position){
				case 0: return getString(R.string.tab_follower);
				case 1: return getString(R.string.tab_following);
				default: throw new IndexOutOfBoundsException("position");
			}
		}

		@NonNull @Override public Fragment getItem(int position){
			switch(position){
				case 0: return followerFragment;
				case 1: return followingFragment;
				default: throw new IndexOutOfBoundsException("position");
			}
		}

		@Override public int getCount(){
			return 2;
		}
	}
}