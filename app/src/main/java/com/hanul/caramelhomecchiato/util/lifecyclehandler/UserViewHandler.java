package com.hanul.caramelhomecchiato.util.lifecyclehandler;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.ComponentActivity;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.gson.JsonObject;
import com.hanul.caramelhomecchiato.R;
import com.hanul.caramelhomecchiato.activity.ProfileActivity;
import com.hanul.caramelhomecchiato.data.User;
import com.hanul.caramelhomecchiato.event.FollowingEvent;
import com.hanul.caramelhomecchiato.event.Ticket;
import com.hanul.caramelhomecchiato.network.UserService;
import com.hanul.caramelhomecchiato.util.BaseCallback;
import com.hanul.caramelhomecchiato.util.GlideUtils;
import com.hanul.caramelhomecchiato.util.SignatureManagers;
import com.hanul.caramelhomecchiato.widget.FollowButton;

import retrofit2.Call;
import retrofit2.Response;

public class UserViewHandler{
	private static final String TAG = "UserViewHandler";

	private final Context context;

	private final ImageView imageViewProfile;
	private final TextView textViewUserName;
	@Nullable private final View userLayout;
	private final FollowButton buttonFollow;

	@Nullable private User user;
	@Nullable private Ticket ticket;

	@Nullable @ColorInt private Integer color;

	public UserViewHandler(ComponentActivity activity){
		this(activity,
				activity.findViewById(R.id.imageViewProfile),
				activity.findViewById(R.id.textViewUserName),
				activity.findViewById(R.id.userLayout),
				activity.findViewById(R.id.buttonFollow));
	}
	public UserViewHandler(View rootView){
		this(rootView.getContext(),
				rootView.findViewById(R.id.imageViewProfile),
				rootView.findViewById(R.id.textViewUserName),
				rootView.findViewById(R.id.userLayout),
				rootView.findViewById(R.id.buttonFollow));
	}
	public UserViewHandler(Context context,
	                       ImageView imageViewProfile,
	                       TextView textViewUserName,
	                       @Nullable View userLayout,
	                       FollowButton buttonFollow){
		this.context = context;
		this.imageViewProfile = imageViewProfile;
		this.textViewUserName = textViewUserName;
		this.userLayout = userLayout;

		this.buttonFollow = buttonFollow;

		if(this.userLayout!=null)
			this.userLayout.setOnClickListener(v -> {
				if(this.user!=null){
					this.context.startActivity(new Intent(this.context, ProfileActivity.class)
							.putExtra(ProfileActivity.EXTRA_USER_ID, user.getId()));
				}
			});
		this.buttonFollow.setOnClickListener(v -> {
			if(this.user==null) return;

			Boolean followedByYou = this.user.getFollowedByYou();
			if(followedByYou==null) return;

			boolean following = !followedByYou;
			UserService.INSTANCE.setFollowing(this.user.getId(), following).enqueue(new BaseCallback(){
				@Override public void onSuccessfulResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response, @NonNull JsonObject result){}
				@Override public void onErrorResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response, @NonNull String error){
					Log.e(TAG, "setFollowing: 예상치 못한 오류: "+error);
					toastError();
				}
				@Override public void onFailedResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response){
					Log.e(TAG, "Failure : "+response.errorBody());
					toastError();
				}
				@Override public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t){
					Log.e(TAG, "deletePost: Failure ", t);
					toastError();
				}

				private void toastError(){
					Toast.makeText(UserViewHandler.this.context, "예상치 못한 오류로 인해 팔로우에 실패했습니다.", Toast.LENGTH_SHORT).show();
				}
			});
			FollowingEvent.dispatch(user.getId(), following);
		});
	}

	public UserViewHandler setNameColor(@ColorInt int color){
		this.color = color;
		return this;
	}

	@Nullable public User getUser(){
		return this.user;
	}
	public void setUser(@Nullable User user){
		setUser(user, true);
	}
	public void setUser(@Nullable User user, boolean subscribeFollowEvent){
		this.user = user;

		if(user!=null){
			Glide.with(context)
					.load(user.getProfileImage())
					.apply(GlideUtils.profileImage())
					.signature(SignatureManagers.PROFILE_IMAGE.getKeyForId(user.getId()))
					.transition(DrawableTransitionOptions.withCrossFade())
					.into(imageViewProfile);

			textViewUserName.setText(user.getName());
			if(color!=null) textViewUserName.setTextColor(color);

			if(subscribeFollowEvent) subscribeFollowEvent();
		}
	}
	public void unsubscribeFollowEvent(){
		if(ticket!=null){
			ticket.unsubscribe();
			ticket = null;
		}
	}
	public void subscribeFollowEvent(){
		unsubscribeFollowEvent();
		if(user!=null){
			Boolean followedByYou = user.getFollowedByYou();
			if(followedByYou!=null){
				buttonFollow.setVisibility(View.VISIBLE);
				ticket = FollowingEvent.subscribe(user.getId(), following -> {
					this.user.setFollowedByYou(following);
					buttonFollow.setFollowing(following);
					buttonFollow.setText(context.getString(following ? R.string.button_following : R.string.button_follow));
					buttonFollow.refreshDrawableState();
				});
				return;
			}
		}
		buttonFollow.setVisibility(View.GONE);
	}
}
