package com.hanul.caramelhomecchiato;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.hanul.caramelhomecchiato.fragment.FindEmailPasswordFragment;
import com.hanul.caramelhomecchiato.fragment.FindPhonePasswordFragment;

import java.util.ArrayList;
import java.util.List;

public class FindPasswordActivity extends AppCompatActivity {
	private ViewPager viewPager;
	private ViewPagerAdapter pagerAdapter;
	private TabLayout tabLayout;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_find_password);

		viewPager = findViewById(R.id.viewPager);
		pagerAdapter = new FindPasswordActivity.ViewPagerAdapter(getSupportFragmentManager());

		viewPager.setAdapter(pagerAdapter);

		tabLayout = findViewById(R.id.tabLayout);
		tabLayout.setupWithViewPager(viewPager);
	}

	private static final class ViewPagerAdapter extends FragmentStatePagerAdapter {
		private ArrayList<Fragment> items = new ArrayList<>();
		private ArrayList<String> tabText = new ArrayList<String>();

		public ViewPagerAdapter(@NonNull FragmentManager fm) {
			super(fm);
			items.add(new FindPhonePasswordFragment());
			items.add(new FindEmailPasswordFragment());

			tabText.add("휴대폰번호로 찾기");
			tabText.add("이메일로 찾기");
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
		public Fragment getItem(int position) { return items.get(position); }

		@Override
		public int getCount() { return items.size(); }
	}
}