package com.hanul.caramelhomecchiato.fragment;

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
import com.hanul.caramelhomecchiato.R;
import com.hanul.caramelhomecchiato.data.User;
import com.hanul.caramelhomecchiato.util.GlideUtils;

public class UserFragment extends Fragment{ // TODO ??
	public static final String ARG_USER = "user";

	public static UserFragment newInstance(User user){
		UserFragment f = new UserFragment();
		Bundle args = new Bundle();
		args.putParcelable(ARG_USER, user);
		f.setArguments(args);
		return f;
	}

	private ImageView imageViewProfilePic;
	private TextView textViewName;

	@Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater,
	                                             @Nullable ViewGroup container,
	                                             @Nullable Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.fragment_user, container, false);
		imageViewProfilePic = view.findViewById(R.id.imageViewProfilePic);
		textViewName = view.findViewById(R.id.textViewName);

		Bundle arguments = getArguments();
		if(arguments!=null){
			User user = arguments.getParcelable(ARG_USER);
			if(user!=null){
				// Set profile image
				Glide.with(this)
						.load(user.getProfileImage())
						.apply(GlideUtils.PROFILE_IMAGE)
						.into(imageViewProfilePic);
				// Set name
				textViewName.setText(user.getName());
			}
		}

		return view;
	}
}
