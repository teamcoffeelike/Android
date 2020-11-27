package com.hanul.caramelhomecchiato.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.hanul.caramelhomecchiato.R;

public class SimpleImageFragment extends Fragment{
	@Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.fragment_simple_image, container, false);
		ImageView imageView = view.findViewById(R.id.imageView);
		Bundle args = getArguments();
		if(args!=null){
			Uri image = args.getParcelable("image");
			if(image!=null) imageView.setImageURI(image);
		}
		return view;
	}
}