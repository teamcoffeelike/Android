package com.hanul.caramelhomecchiato.event;

@FunctionalInterface
public interface OnLikeChanged{
	void onLikeChanged(int likes, boolean likedByYou);
}
