package com.hanul.caramelhomecchiato.adapter;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
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

		private final ImageButton buttonPostOption;
		
		public ViewHolder(@NonNull View itemView){
			super(itemView);
			imageViewPostUserProfile = itemView.findViewById(R.id.imageViewPostUserProfile);
			textViewPostUser = itemView.findViewById(R.id.textViewPostUser);
			images = itemView.findViewById(R.id.images);
			textViewPost = itemView.findViewById(R.id.textViewPost);
			textViewLikes = itemView.findViewById(R.id.textViewLikes);
			textViewComments = itemView.findViewById(R.id.textViewComments);
			buttonPostOption = itemView.findViewById(R.id.buttonPostOption);

			/* 포스트 수정/삭제 버튼 클릭 이벤트 */
			// TODO 작성자에게만 수정/삭제 권한이 있어야함!
			buttonPostOption.setOnClickListener(v -> {
				PopupMenu popupMenu = new PopupMenu(itemView.getContext(), v);
				popupMenu.getMenuInflater().inflate(R.menu.post_menu, popupMenu.getMenu());

				popupMenu.setOnMenuItemClickListener(item -> {
					switch (item.getItemId()) {
						case R.id.postModify:
							Toast.makeText(itemView.getContext(), "글 수정 버튼 클릭", Toast.LENGTH_SHORT).show();
							break;
						case R.id.postDelete:
							Toast.makeText(itemView.getContext(), "글 삭제 버튼 클릭", Toast.LENGTH_SHORT).show();
							break;
					}
					return true;
				});
				popupMenu.show();
			});
			
			itemView.findViewById(R.id.buttonLike).setOnClickListener(v -> {
				Toast.makeText(this.itemView.getContext(), ";)", Toast.LENGTH_SHORT).show();
			});
		}

		@Override protected void setItem(int position, Post post){ // TODO 프로필 이미지와 첨부 사진
			textViewPostUser.setText(post.getUser().getName());
			textViewPost.setText(post.getText());

			textViewLikes.setText(itemView.getContext().getString(R.string.n_likes, post.getLikes()));
			// TODO 댓글이 없을 시에는 댓글보기 없어야 함
		}
	}
}
