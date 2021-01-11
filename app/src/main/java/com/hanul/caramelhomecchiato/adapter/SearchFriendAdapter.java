package com.hanul.caramelhomecchiato.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.hanul.caramelhomecchiato.R;
import com.hanul.caramelhomecchiato.data.User;
import com.hanul.caramelhomecchiato.util.GlideUtils;

public class SearchFriendAdapter extends BaseAdapter<User>{
	@NonNull @Override public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
		return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_search, parent, false));
	}


	public static final class ViewHolder extends BaseAdapter.ViewHolder<User>{
		private final ImageView userProfileImage;
		private final TextView userName;
		private final Button followButton;

		public ViewHolder(@NonNull View itemView){
			super(itemView);
			userProfileImage = itemView.findViewById(R.id.imageViewUserProfile);
			userName = itemView.findViewById(R.id.textViewUserName);
			followButton = itemView.findViewById(R.id.followButton);

			followButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {

				}
			});
		}

		@Override protected void setItem(int position, User user){
			Glide.with(itemView)
					.load(user.getProfileImage())
					.apply(GlideUtils.profileImage())
					.transition(DrawableTransitionOptions.withCrossFade())
					.into(userProfileImage);
			userName.setText(user.getName());
		}
	}
}


