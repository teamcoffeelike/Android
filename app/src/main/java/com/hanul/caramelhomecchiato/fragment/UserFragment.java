package com.hanul.caramelhomecchiato.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.hanul.caramelhomecchiato.R;
import com.hanul.caramelhomecchiato.activity.ProfileActivity;
import com.hanul.caramelhomecchiato.data.User;
import com.hanul.caramelhomecchiato.util.GlideUtils;

public class UserFragment extends Fragment{
	public static final String EXTRA_USER = "user";
	private User user;

	public static UserFragment newInstance(User user){
		UserFragment f = new UserFragment();
		Bundle args = new Bundle();
		args.putParcelable(EXTRA_USER, user);
		f.setArguments(args);
		return f;
	}

	private ImageView imageViewProfile;
	private TextView textViewUserName;

	@Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater,
	                                             @Nullable ViewGroup container,
	                                             @Nullable Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.fragment_user, container, false);
		imageViewProfile = view.findViewById(R.id.imageViewProfile);
		textViewUserName = view.findViewById(R.id.textViewUserName);

		view.findViewById(R.id.userLayout).setOnClickListener(v -> {
			if(this.user!=null){
				startActivity(new Intent(getContext(), ProfileActivity.class).putExtra(ProfileActivity.EXTRA_USER_ID, user.getId()));
			}
		});

		Bundle arguments = getArguments();
		User user = null;
		if(arguments!=null){
			User u = arguments.getParcelable(EXTRA_USER);
			if(u!=null) user = u;
		}
		if(user==null&&savedInstanceState!=null){
			User u = savedInstanceState.getParcelable(EXTRA_USER);
			if(u!=null) user = u;
		}

		if(user!=null) setUser(user);

		return view;
	}

	@Override public void onSaveInstanceState(@NonNull Bundle outState){
		super.onSaveInstanceState(outState);
		if(user!=null) outState.putParcelable(EXTRA_USER, user);
	}

	public void setUser(User user){
		this.user = user;

		Glide.with(this)
				.load(user.getProfileImage())
				.apply(GlideUtils.profileImage())
				.transition(DrawableTransitionOptions.withCrossFade())
				.into(imageViewProfile);

		textViewUserName.setText(user.getName());
	}
}
