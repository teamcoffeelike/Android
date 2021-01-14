package com.hanul.caramelhomecchiato.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.JsonObject;
import com.hanul.caramelhomecchiato.R;
import com.hanul.caramelhomecchiato.data.UserProfile;
import com.hanul.caramelhomecchiato.fragment.ProfileFragment;
import com.hanul.caramelhomecchiato.fragment.PostListFragment;
import com.hanul.caramelhomecchiato.fragment.RecipeCategoryFragment;
import com.hanul.caramelhomecchiato.fragment.TimerFragment;
import com.hanul.caramelhomecchiato.network.NetUtils;
import com.hanul.caramelhomecchiato.network.UserService;
import com.hanul.caramelhomecchiato.util.Auth;
import com.hanul.caramelhomecchiato.util.BaseCallback;
import com.hanul.caramelhomecchiato.util.GlideUtils;

import retrofit2.Call;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity{
	private static final String TAG = "MainActivity";

	private PostListFragment postListFragment;
	private RecipeCategoryFragment recipeCategoryFragment;
	private TimerFragment timerFragment;
	private ProfileFragment profileFragment;

	private TextView textViewProfileName;
	private ImageView imageViewProfile;
	private DrawerLayout drawerLayout;
	private ImageView imageViewAppBarUserProfile;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		textViewProfileName = findViewById(R.id.textViewProfileName);
		imageViewProfile = findViewById(R.id.imageViewProfile);

		postListFragment = new PostListFragment();
		recipeCategoryFragment = new RecipeCategoryFragment();
		timerFragment = new TimerFragment();
		profileFragment = new ProfileFragment();

		// 툴바 셋업
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		drawerLayout = findViewById(R.id.drawerLayout);
		ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
				this,
				drawerLayout,
				toolbar,
				R.string.drawer_open,
				R.string.drawer_close);
		toggle.setDrawerIndicatorEnabled(false);
		drawerLayout.addDrawerListener(toggle);
		toggle.syncState();

		imageViewAppBarUserProfile = findViewById(R.id.imageViewAppBarUserProfile);
		imageViewAppBarUserProfile.setOnClickListener(v -> {
			if(drawerLayout.isDrawerVisible(GravityCompat.END)) drawerLayout.closeDrawer(GravityCompat.END);
			else drawerLayout.openDrawer(GravityCompat.END);
		});

		// 내비
		findViewById(R.id.nav).setClickable(true);

		findViewById(R.id.myRecipesMenu).setOnClickListener(v -> {
			startActivity(new Intent(this, RecipeListActivity.class)
					.putExtra(RecipeListActivity.EXTRA_MY_RECIPE, true));
		});
		findViewById(R.id.followsMenu).setOnClickListener(v -> startActivity(new Intent(this, FollowsActivity.class)));
		findViewById(R.id.likesMenu).setOnClickListener(v -> startActivity(new Intent(this, LikesActivity.class)));
		findViewById(R.id.searchFriendsMenu).setOnClickListener(v -> startActivity(new Intent(this, SearchFriendActivity.class)));
		findViewById(R.id.settingsMenu).setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));

		// BottomNavigation & 프래그먼트 셋업
		BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
		bottomNav.setOnNavigationItemSelectedListener(item -> {
			int id = item.getItemId();
			if(id==R.id.posts) show(postListFragment);
			else if(id==R.id.recipes) show(recipeCategoryFragment);
			else if(id==R.id.timer) show(timerFragment);
			else if(id==R.id.profile) show(profileFragment);
			else{
				Log.e(TAG, "onCreate: Invalid navigation item "+id);
				return false;
			}
			return true;
		});

		setProfile(null);

		show(postListFragment);
	}

	@Override protected void onResume(){
		super.onResume();
		UserService.INSTANCE.profile(Auth.getInstance().expectLoginUser()).enqueue(new BaseCallback(){
			@Override public void onSuccessfulResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response, @NonNull JsonObject result){
				setProfile(NetUtils.GSON.fromJson(result, UserProfile.class));
			}
			@Override public void onErrorResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response, @NonNull String error){
				Log.e(TAG, "profile: 예상치 못한 오류: "+error);
				Toast.makeText(MainActivity.this, "예상치 못한 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
			}
			@Override public void onFailedResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response){
				Log.e(TAG, "profile: 요청 실패 ");
				Toast.makeText(MainActivity.this, "예상치 못한 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
			}
			@Override public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t){
				Log.e(TAG, "profile: Failure ", t);
				Toast.makeText(MainActivity.this, "예상치 못한 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
			}
		});
	}

	public void setProfile(@Nullable UserProfile profile){
		textViewProfileName.setText(profile==null ? "" : profile.getUser().getName());

		Uri profileImage = profile==null ? null : profile.getUser().getProfileImage();

		Glide.with(this)
				.load(profileImage)
				.apply(GlideUtils.profileImage())
				.transition(DrawableTransitionOptions.withCrossFade())
				.into(imageViewAppBarUserProfile);
		Glide.with(this)
				.load(profileImage)
				.apply(GlideUtils.profileImage())
				.transition(DrawableTransitionOptions.withCrossFade())
				.into(imageViewProfile);

		profileFragment.setProfile(profile);
	}

	@Override public boolean onKeyDown(int keyCode, KeyEvent event){
		if(keyCode==KeyEvent.KEYCODE_BACK&&drawerLayout.isDrawerVisible(GravityCompat.END)){
			drawerLayout.closeDrawer(GravityCompat.END);
			return true;
		}else return super.onKeyDown(keyCode, event);
	}

	private void show(Fragment f){
		getSupportFragmentManager()
				.beginTransaction()
				.replace(R.id.mainFrame, f)
				.commit();
	}
}