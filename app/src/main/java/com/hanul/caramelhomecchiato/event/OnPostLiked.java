package com.hanul.caramelhomecchiato.event;

@FunctionalInterface
public interface OnPostLiked{
	void onPostLiked(boolean likedByYou, int likes);
}
