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
	public static final String EXTRA_IMAGE = "image";

	private ImageView imageView;

	@Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.fragment_simple_image, container, false);
		imageView = view.findViewById(R.id.imageView);
		Bundle args = getArguments();
		if(args!=null){
			Uri image = args.getParcelable(EXTRA_IMAGE);
			if(image!=null) imageView.setImageURI(image);
		}
		return view;
	}

	public void setImage(Uri image){
		if(getFragmentManager()!=null&&isStateSaved()){
			imageView.setImageURI(image);
		}else{
			Bundle args = new Bundle();
			args.putParcelable(EXTRA_IMAGE, image);
			setArguments(args);
		}
	}
}