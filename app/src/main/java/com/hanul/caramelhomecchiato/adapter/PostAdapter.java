package com.hanul.caramelhomecchiato.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

import com.hanul.caramelhomecchiato.R;
import com.hanul.caramelhomecchiato.data.Post;

public class PostAdapter extends BaseAdapter<Post>{
	@NonNull @Override public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
		return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_post, parent, false));
	}


	public static final class ViewHolder extends BaseAdapter.ViewHolder<Post>{
		private final ImageView imageViewPostUserProfile;
		private final TextView textViewPostUser;
		private final ViewPager images;
		private final TextView textViewPost;

		private final TextView textViewLikes;
		private final TextView textViewComments;

		public ViewHolder(@NonNull View itemView){
			super(itemView);
			imageViewPostUserProfile = itemView.findViewById(R.id.imageViewPostUserProfile);
			textViewPostUser = itemView.findViewById(R.id.textViewPostUser);
			images = itemView.findViewById(R.id.images);
			textViewPost = itemView.findViewById(R.id.textViewPost);
			textViewLikes = itemView.findViewById(R.id.textViewLikes);
			textViewComments = itemView.findViewById(R.id.textViewComments);

			itemView.findViewById(R.id.buttonLike).setOnClickListener(v -> {
				Toast.makeText(this.itemView.getContext(), ";)", Toast.LENGTH_SHORT).show();
			});
		}

		@Override protected void setItem(int position, Post post){ // TODO 프로필 이미지와 첨부 사진
			textViewPostUser.setText(post.getUser().getName());
			textViewPost.setText(post.getText());

			textViewLikes.setText(itemView.getContext().getString(R.string.likes, post.getLikes()));
			// TODO 댓글이 없을 시에는 댓글보기 없어야 함
		}
	}
}
