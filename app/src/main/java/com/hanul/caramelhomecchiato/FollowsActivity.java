package com.hanul.caramelhomecchiato;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.hanul.caramelhomecchiato.adapter.ViewPagerAdapter;
import com.hanul.caramelhomecchiato.fragment.FollowerFragment;
import com.hanul.caramelhomecchiato.fragment.FollowingFragment;

public class FollowsActivity extends AppCompatActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_follows);

		ViewPager viewPager = findViewById(R.id.viewPager);
		ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

		viewPager.setAdapter(viewPagerAdapter);

		TabLayout tabLayout = findViewById(R.id.tabLayout);
		tabLayout.setupWithViewPager(viewPager);

	}
}