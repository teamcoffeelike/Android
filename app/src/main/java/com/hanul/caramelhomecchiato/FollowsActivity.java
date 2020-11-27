package com.hanul.caramelhomecchiato;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.hanul.caramelhomecchiato.fragment.FollowerFragment;
import com.hanul.caramelhomecchiato.fragment.FollowingFragment;

import java.util.ArrayList;

public class FollowsActivity extends AppCompatActivity{
	private ViewPager viewPager;
	private ViewPagerAdapter pagerAdapter;
	private TabLayout tabLayout;


	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_follows);

		viewPager = findViewById(R.id.viewPager);
		pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

		viewPager.setAdapter(pagerAdapter);

		tabLayout = findViewById(R.id.tabLayout);
		tabLayout.setupWithViewPager(viewPager);

	}

	private static final class ViewPagerAdapter extends FragmentStatePagerAdapter {
		private ArrayList<Fragment> items = new ArrayList<>();
		private ArrayList<String> tabText = new ArrayList<String>();

		public ViewPagerAdapter(@NonNull FragmentManager fm) {
			super(fm);
			items.add(new FollowerFragment());
			items.add(new FollowingFragment());

			tabText.add("팔로워");
			tabText.add("팔로잉");
		}

		@Nullable
		@Override
		public CharSequence getPageTitle(int position) {
			return tabText.get(position);
		}

		public void addItem(Fragment item) {
			items.add(item);
		}

		@NonNull
		@Override
		public Fragment getItem(int position) {
			return items.get(position);
		}

		@Override
		public int getCount() {
			return items.size();
		}
	}
}