package com.hanul.caramelhomecchiato.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.hanul.caramelhomecchiato.R;
import com.hanul.caramelhomecchiato.data.FindPassword;
import com.hanul.caramelhomecchiato.data.Post;

public class FindPasswordAdapter extends BaseAdapter<FindPassword>{
	@NonNull @Override public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
		return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_, parent, false));
	}


	public static final class ViewHolder extends BaseAdapter.ViewHolder<FindPassword>{
		private final ImageView imageViewPost;

		public ViewHolder(@NonNull View itemView){
			super(itemView);
			imageViewPost = itemView.findViewById(R.id.imageViewPost);
			imageViewPost.setOnClickListener(v -> {
				Toast.makeText(this.itemView.getContext(), ""+this.getLayoutPosition(), Toast.LENGTH_SHORT).show();
			});
		}

		@Override protected void setItem(int position, Post post){
			// TODO 첨부 사진
		}
	}
}
