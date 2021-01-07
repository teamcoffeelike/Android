package com.hanul.caramelhomecchiato.adapter;

import android.app.AlertDialog;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;

import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;
import com.hanul.caramelhomecchiato.R;
import com.hanul.caramelhomecchiato.activity.ReactionActivity;
import com.hanul.caramelhomecchiato.data.Post;
import com.hanul.caramelhomecchiato.network.PostService;
import com.hanul.caramelhomecchiato.util.Auth;
import com.hanul.caramelhomecchiato.util.BaseCallback;

import retrofit2.Call;
import retrofit2.Response;

public class PostAdapter extends BaseAdapter<Post>{
	private static final String TAG = "PostAdapter";

	@NonNull @Override public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
		return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_post, parent, false));
	}


	public static final class ViewHolder extends BaseAdapter.ViewHolder<Post>{
		private final ImageView imageViewPostUserProfile;
		private final TextView textViewPostUser;
		private final ImageView imageViewPost;
		private final TextView textViewPost;

		private final View buttonLike;

		private final TextView textViewLikes;
		private final TextView textViewComments;

		private final ImageButton buttonPostOption;

		public ViewHolder(@NonNull View itemView){
			super(itemView);
			imageViewPostUserProfile = itemView.findViewById(R.id.imageViewPostUserProfile);
			textViewPostUser = itemView.findViewById(R.id.textViewPostUser);
			imageViewPost = itemView.findViewById(R.id.imageViewPost);
			textViewPost = itemView.findViewById(R.id.textViewPost);

			buttonLike = itemView.findViewById(R.id.buttonLike);

			textViewLikes = itemView.findViewById(R.id.textViewLikes);
			textViewComments = itemView.findViewById(R.id.textViewComments);

			buttonPostOption = itemView.findViewById(R.id.buttonPostOption);

			/* 포스트 수정/삭제 버튼 클릭 이벤트 */
			// TODO 작성자에게만 수정/삭제 권한이 있어야함!
			buttonPostOption.setOnClickListener(v -> {
				PopupMenu popupMenu = new PopupMenu(itemView.getContext(), v);
				popupMenu.getMenuInflater().inflate(R.menu.post_menu, popupMenu.getMenu());

				popupMenu.setOnMenuItemClickListener(item -> {
					int itemId = item.getItemId();
					if(itemId==R.id.postModify){// TODO
						Toast.makeText(itemView.getContext(), "글 수정 버튼 클릭", Toast.LENGTH_SHORT).show();
					}else if(itemId==R.id.postDelete){
						new AlertDialog.Builder(itemView.getContext())
								.setTitle("정말 삭제하시겠습니까?")
								.setPositiveButton("예", (dialog, which) -> {
									PostService.INSTANCE.deletePost(getItem().getId()).enqueue(new BaseCallback(){
										@Override public void onSuccessfulResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response, @NonNull JsonObject result){
											Toast.makeText(itemView.getContext(), "포스트를 삭제했습니다.", Toast.LENGTH_SHORT).show();
										}
										@Override public void onErrorResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response, @NonNull String error){
											Log.e(TAG, "deletePost: 예상치 못한 오류: "+error);
											Toast.makeText(itemView.getContext(), "예상치 못한 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
										}
										@Override public void onFailedResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response){
											Log.e(TAG, "Failure : "+response.errorBody());
											Toast.makeText(itemView.getContext(), "예상치 못한 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
										}
										@Override public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t){
											Log.e(TAG, "deletePost: Failure ", t);
											Toast.makeText(itemView.getContext(), "예상치 못한 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
										}
									});
								})
								.setNegativeButton("아니오", (dialog, which) -> {})
								.show();
					}
					return true;
				});
				popupMenu.show();
			});

			buttonLike.setOnClickListener(v -> {
				Toast.makeText(this.itemView.getContext(), ";)", Toast.LENGTH_SHORT).show();
			});

			/* 댓글보기 클릭 -> 댓글창으로 이동(댓글 목록, 댓글 쓰기) */
			textViewComments.setOnClickListener(v -> {
				Intent intent = new Intent(v.getContext(), ReactionActivity.class);
				v.getContext().startActivity(intent);
			});
		}

		@Override protected void setItem(int position, Post post){
			if(post.getAuthor().getProfileImage()!=null){
				Glide.with(itemView)
						.load(R.drawable.default_profile_image)
						.placeholder(R.drawable.default_profile_image)
						.circleCrop()
						.into(imageViewPostUserProfile);
			}else{
				Glide.with(itemView)
						.load(R.drawable.default_profile_image)
						.circleCrop()
						.into(imageViewPostUserProfile);
			}
			textViewPostUser.setText(post.getAuthor().getName());

			if(post.getImage()!=null){
				Glide.with(itemView)
						.load(post.getImage())
						.into(imageViewPost);
			}

			textViewPost.setText(post.getText());

			textViewLikes.setText(itemView.getContext().getString(R.string.n_likes, post.getLikes()));

			buttonPostOption.setVisibility(
					post.getAuthor().getId()!=Auth.getInstance().expectLoginUser() ?
							View.INVISIBLE :
							View.VISIBLE); // TODO ????
		}
	}
}
