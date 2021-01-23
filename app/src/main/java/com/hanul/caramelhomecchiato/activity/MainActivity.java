package com.hanul.caramelhomecchiato.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.JsonObject;
import com.hanul.caramelhomecchiato.R;
import com.hanul.caramelhomecchiato.data.UserProfile;
import com.hanul.caramelhomecchiato.event.ProfileImageChangeEvent;
import com.hanul.caramelhomecchiato.event.Ticket;
import com.hanul.caramelhomecchiato.fragment.PostListFragment;
import com.hanul.caramelhomecchiato.fragment.ProfileFragment;
import com.hanul.caramelhomecchiato.fragment.RecipeCategoryFragment;
import com.hanul.caramelhomecchiato.fragment.TimerFragment;
import com.hanul.caramelhomecchiato.network.NetUtils;
import com.hanul.caramelhomecchiato.network.UserService;
import com.hanul.caramelhomecchiato.util.Auth;
import com.hanul.caramelhomecchiato.util.BaseCallback;
import com.hanul.caramelhomecchiato.util.GlideUtils;
import com.hanul.caramelhomecchiato.util.SignatureManagers;

import retrofit2.Call;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity{
	private static final String TAG = "MainActivity";

	private static final String SAVED_STATE_MENU_INDEX = "menuIndex";

	private static final long BACK_PRESS_TIME = 10000;

	private PostListFragment postListFragment;
	private RecipeCategoryFragment recipeCategoryFragment;
	private TimerFragment timerFragment;
	private ProfileFragment profileFragment;

	private TextView textViewProfileName;
	private ImageView imageViewProfile;
	private DrawerLayout drawerLayout;
	private ImageView imageViewAppBarUserProfile;

	@Nullable private UserProfile profile;

	@SuppressWarnings("unused") private final Ticket profileImageChangedTicket = ProfileImageChangeEvent.subscribe(this::redrawProfileImage);

	private int menuIndex = -1;

	@Nullable private Long backPressedTime;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState){
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
					.putExtra(RecipeListActivity.EXTRA_AUTHOR, Auth.getInstance().expectLoginUser()));
		});
		findViewById(R.id.followsMenu).setOnClickListener(v -> startActivity(new Intent(this, FollowsActivity.class)));
		findViewById(R.id.likesMenu).setOnClickListener(v -> startActivity(new Intent(this, LikesActivity.class)));
		findViewById(R.id.searchFriendsMenu).setOnClickListener(v -> startActivity(new Intent(this, SearchFriendActivity.class)));
		findViewById(R.id.settingsMenu).setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));
		findViewById(R.id.warmerMenu).setOnClickListener(v -> startActivity(new Intent(this, WarmerActivity.class)));

		// BottomNavigation & 프래그먼트 셋업
		BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
		bottomNav.setOnNavigationItemSelectedListener(item -> {
			int id = item.getItemId();
			if(id==R.id.posts) show(0);
			else if(id==R.id.recipes) show(1);
			else if(id==R.id.timer) show(2);
			else if(id==R.id.profile) show(3);
			else{
				Log.e(TAG, "onCreate: Invalid navigation item "+id);
				return false;
			}
			return true;
		});

		setProfile(null);

		show(0);
	}

	@Override protected void onSaveInstanceState(@NonNull Bundle outState){
		super.onSaveInstanceState(outState);
		outState.putInt(SAVED_STATE_MENU_INDEX, menuIndex);
	}

	@Override protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState){
		super.onRestoreInstanceState(savedInstanceState);
		show(savedInstanceState.getInt(SAVED_STATE_MENU_INDEX, 0));
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

	@Override public void onBackPressed(){
		if(drawerLayout.isDrawerVisible(GravityCompat.END)){
			drawerLayout.closeDrawer(GravityCompat.END);
			return;
		}
		long time = System.currentTimeMillis();
		if(backPressedTime==null||(time-backPressedTime)>BACK_PRESS_TIME){
			backPressedTime = time;
			Toast.makeText(this, "뒤로가기 버튼을 한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
			return;
		}
		super.onBackPressed();
	}

	public void setProfile(@Nullable UserProfile profile){
		this.profile = profile;
		textViewProfileName.setText(profile==null ? "" : this.profile.getUser().getName());

		redrawProfileImage();

		profileFragment.setProfile(profile);
	}

	private void show(int menuIndex){
		show(menuIndex, false);
	}
	private void show(int menuIndex, boolean force){
		if(this.menuIndex!=menuIndex||force){
			this.menuIndex = menuIndex;

			Fragment fragment;
			switch(menuIndex){
				case 0:
					fragment = this.postListFragment;
					break;
				case 1:
					fragment = this.recipeCategoryFragment;
					break;
				case 2:
					fragment = this.timerFragment;
					break;
				case 3:
					fragment = this.profileFragment;
					break;
				default:
					throw new IllegalArgumentException("menuIndex");
			}

			getSupportFragmentManager()
					.beginTransaction()
					.replace(R.id.mainFrame, fragment)
					.commit();
		}
	}

	private void redrawProfileImage(){
		Uri profileImage = profile==null ? null : profile.getUser().getProfileImage();

		if(profileImage!=null){
			Key signature = SignatureManagers.PROFILE_IMAGE.getKeyForId(profile.getUser().getId());
			Glide.with(this)
					.load(profileImage)
					.apply(GlideUtils.profileImage())
					.signature(signature)
					.transition(DrawableTransitionOptions.withCrossFade())
					.into(imageViewAppBarUserProfile);
			Glide.with(this)
					.load(profileImage)
					.apply(GlideUtils.profileImage())
					.signature(signature)
					.transition(DrawableTransitionOptions.withCrossFade())
					.into(imageViewProfile);
		}else{
			Glide.with(this)
					.load((Uri)null)
					.apply(GlideUtils.profileImage())
					.transition(DrawableTransitionOptions.withCrossFade())
					.into(imageViewAppBarUserProfile);
			Glide.with(this)
					.load((Uri)null)
					.apply(GlideUtils.profileImage())
					.transition(DrawableTransitionOptions.withCrossFade())
					.into(imageViewProfile);
		}
	}
}