package com.hanul.caramelhomecchiato.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.hanul.caramelhomecchiato.fragment.FollowerFragment;
import com.hanul.caramelhomecchiato.fragment.FollowingFragment;

import java.util.ArrayList;

public class ViewPagerAdapter extends FragmentPagerAdapter {
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
