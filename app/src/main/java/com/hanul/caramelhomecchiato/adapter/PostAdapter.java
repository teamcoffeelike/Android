package com.hanul.caramelhomecchiato.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.hanul.caramelhomecchiato.R;
import com.hanul.caramelhomecchiato.data.Post;
import com.hanul.caramelhomecchiato.util.lifecyclehandler.PostViewHandler;

import java.util.HashSet;
import java.util.Set;

public class PostAdapter extends BaseAdapter<Post>{
	private final Set<ViewHolder> viewHolders = new HashSet<>();

	@NonNull @Override public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
		return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_post, parent, false));
	}

	@Override public void onViewAttachedToWindow(@NonNull BaseAdapter.ViewHolder<Post> holder){
		if(holder instanceof ViewHolder){
			ViewHolder h = (ViewHolder)holder;
			h.postViewHandler.subscribeFollowEvent();
			viewHolders.add(h);
		}
	}

	@Override public void onViewRecycled(@NonNull BaseAdapter.ViewHolder<Post> holder){
		if(holder instanceof ViewHolder){
			((ViewHolder)holder).postViewHandler.unsubscribeFollowEvent();
			viewHolders.remove(holder);
		}
	}

	public void clearEvents(){
		for(ViewHolder viewHolder : viewHolders){
			viewHolder.postViewHandler.unsubscribeFollowEvent();
		}
		viewHolders.clear();
	}


	public static final class ViewHolder extends BaseAdapter.ViewHolder<Post>{
		private static int incr;

		private final int hash = incr++;

		private final PostViewHandler postViewHandler;

		public ViewHolder(@NonNull View itemView){
			super(itemView);
			postViewHandler = new PostViewHandler(itemView);
		}

		@Override protected void setItem(int position, Post post){
			postViewHandler.setPost(post);
		}

		@Override public boolean equals(Object o){
			if(this==o) return true;
			if(o==null||getClass()!=o.getClass()) return false;

			ViewHolder that = (ViewHolder)o;

			return hash==that.hash;
		}
		@Override public int hashCode(){
			return hash;
		}
	}
}
