package com.hanul.caramelhomecchiato.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.hanul.caramelhomecchiato.R;
import com.hanul.caramelhomecchiato.data.Post;
import com.hanul.caramelhomecchiato.event.PostDeleteEventDispatcher;
import com.hanul.caramelhomecchiato.event.Ticket;
import com.hanul.caramelhomecchiato.util.lifecyclehandler.PostViewHandler;

import java.util.List;

public class PostAdapter extends BaseAdapter<Post>{
	private final Ticket postDeleteEventTicket = PostDeleteEventDispatcher.subscribeAll(postId -> {
		List<Post> elements = elements();
		for(int i = 0; i<elements.size(); i++){
			Post post = elements.get(i);
			if(post.getId()==postId){
				elements.remove(i);
				notifyItemRemoved(i);
				return;
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
