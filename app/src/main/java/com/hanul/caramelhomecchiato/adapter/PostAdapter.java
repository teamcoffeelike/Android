package com.hanul.caramelhomecchiato.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.hanul.caramelhomecchiato.R;
import com.hanul.caramelhomecchiato.data.Post;
import com.hanul.caramelhomecchiato.event.ProfileImageChangeEvent;
import com.hanul.caramelhomecchiato.event.Ticket;
import com.hanul.caramelhomecchiato.util.Auth;
import com.hanul.caramelhomecchiato.util.lifecyclehandler.PostViewHandler;

import java.util.List;

public class PostAdapter extends AbstractPostAdapter{
	@SuppressWarnings("unused") private final Ticket profileImageChangeTicket = ProfileImageChangeEvent.subscribe(() -> {
		int loginUser = Auth.getInstance().expectLoginUser();
		List<Post> elements = elements();
		for(int i = 0; i<elements.size(); i++){
			Post post = elements.get(i);
			if(post.getAuthor().getId()==loginUser){
				notifyItemChanged(i);
			}
		}
	});

	@NonNull @Override public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
		return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_post, parent, false));
	}


	public static final class ViewHolder extends BaseAdapter.ViewHolder<Post>{
		private final PostViewHandler postViewHandler;

		public ViewHolder(@NonNull View itemView){
			super(itemView);
			postViewHandler = new PostViewHandler(itemView, false);
		}

		@Override protected void setItem(int position, Post post){
			postViewHandler.setPost(post);
		}
	}
}
