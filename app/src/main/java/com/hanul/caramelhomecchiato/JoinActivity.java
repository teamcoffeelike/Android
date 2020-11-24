package com.hanul.caramelhomecchiato;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class JoinActivity extends AppCompatActivity{
	private ViewPager viewPager;
	private PagerAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_join);

		viewPager = findViewById(R.id.viewPager);
		adapter = new PagerAdapter(getSupportFragmentManager());

		viewPager.setAdapter(adapter);
	}

	public void openJoinForm(JoinType type){
		if(viewPager.getCurrentItem()==0){
			switch(type){
			case WITH_KAKAO: // TODO
				Toast.makeText(this, "미완성", Toast.LENGTH_SHORT).show();
				break;
			default:
				adapter.joinForm.setJoinType(type);
				viewPager.setCurrentItem(1);
				break;
			}
		}
	}

	private static final class PagerAdapter extends FragmentStatePagerAdapter{
		public final JoinFragment join = new JoinFragment();
		public final JoinFormFragment joinForm = new JoinFormFragment();

		public PagerAdapter(@NonNull FragmentManager fm){
			super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
		}

		@NonNull @Override public Fragment getItem(int position){
			switch(position){
			case 0:
				return join;
			case 1:
				return joinForm;
			default:
				throw new IndexOutOfBoundsException("position");
			}
		}
		@Override public int getCount(){
			return 2;
		}
	}

	public enum JoinType{
		WITH_PHONE,
		WITH_EMAIL,
		WITH_KAKAO
	}
}