package com.hanul.caramelhomecchiato.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.hanul.caramelhomecchiato.R;
import com.hanul.caramelhomecchiato.activity.PostActivity;
import com.hanul.caramelhomecchiato.data.Post;
import com.hanul.caramelhomecchiato.util.GlideUtils;
import com.hanul.caramelhomecchiato.util.SignatureManagers;

public class ProfilePostAdapter extends AbstractPostAdapter{
	@NonNull @Override public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
		return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_profile_post, parent, false));
	}


	public static final class ViewHolder extends BaseAdapter.ViewHolder<Post>{
		private final ImageView imageViewPost;

		public ViewHolder(@NonNull View itemView){
			super(itemView);
			imageViewPost = itemView.findViewById(R.id.imageViewPost);
			imageViewPost.setOnClickListener(v -> {
				Context ctx = itemView.getContext();
				ctx.startActivity(new Intent(ctx, PostActivity.class)
						.putExtra(PostActivity.EXTRA_POST_ID, getItem().getId()));
			});
		}

		@Override protected void setItem(int position, Post post){
			Glide.with(itemView)
					.load(post.getImage())
					.apply(GlideUtils.postImage())
					.signature(SignatureManagers.POST_IMAGE.getKeyForId(post.getId()))
					.transition(DrawableTransitionOptions.withCrossFade())
					.into(imageViewPost);
		}
	}
}
